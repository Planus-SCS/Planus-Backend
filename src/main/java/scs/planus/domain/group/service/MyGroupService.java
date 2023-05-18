package scs.planus.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.category.dto.TodoCategoryGetResponseDto;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.dto.GroupTagResponseDto;
import scs.planus.domain.group.dto.mygroup.*;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.entity.GroupTag;
import scs.planus.domain.group.repository.GroupMemberQueryRepository;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.group.repository.GroupTagRepository;
import scs.planus.domain.member.dto.MemberResponseDto;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.dto.calendar.TodoDailyDto;
import scs.planus.domain.todo.dto.calendar.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.calendar.TodoDailyScheduleDto;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.util.validator.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static scs.planus.global.exception.CustomExceptionStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MyGroupService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final GroupTagRepository groupTagRepository;
    private final TodoQueryRepository todoQueryRepository;
    private final TodoCategoryRepository todoCategoryRepository;

    public List<GroupBelongInResponseDto> getMyGroupsInDropDown(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        List<GroupMember> groupMembers = groupMemberRepository.findAllByActiveGroupAndMemberId(member.getId());
        List<GroupBelongInResponseDto> responseDtos = groupMembers.stream()
                .map(gm -> GroupBelongInResponseDto.of(gm.getGroup()))
                .collect(Collectors.toList());

        return responseDtos;
    }

    public List<MyGroupResponseDto> getMyAllGroups(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        List<GroupMember> myGroupMembers = groupMemberRepository.findAllByActiveGroupAndMemberId(member.getId());
        List<Group> myGroups = myGroupMembers.stream()
                .map(GroupMember::getGroup)
                .collect(Collectors.toList());

        List<GroupMember> allGroupMembers = groupMemberRepository.findAllGroupMemberInGroups(myGroups);
        List<GroupTag> allGroupTags = groupTagRepository.findAllTagInGroups(myGroups);

        List<MyGroupResponseDto> responseDtos = myGroups.stream().map(group -> {
                    List<GroupTagResponseDto> eachGroupTagDtos = getEachGroupTags(group, allGroupTags);
                    Boolean onlineStatus = isOnlineStatus(member, group, myGroupMembers);
                    int onlineCount = getOnlineCount(group, allGroupMembers);

                    return MyGroupResponseDto.of(group, eachGroupTagDtos, onlineStatus, onlineCount);
                })
                .collect(Collectors.toList());

        return responseDtos;
    }

    public MyGroupDetailResponseDto getMyEachGroupDetail(Long memberId, Long groupId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));

        Boolean isJoined = groupMemberQueryRepository.existByMemberIdAndGroupId(member.getId(), groupId);
        if (!isJoined) {
            throw new PlanusException(NOT_JOINED_GROUP);
        }

        List<GroupMember> myGroupMembers = groupMemberRepository.findAllWithMemberByGroupAndStatus(group);
        List<GroupTag> groupTags = groupTagRepository.findAllByGroup(group);

        List<GroupTagResponseDto> groupTagResponseDtos = groupTags.stream()
                .map(GroupTagResponseDto::of)
                .collect(Collectors.toList());

        Boolean isLeader = isGroupLeader(member, myGroupMembers);
        Boolean onlineStatus = isOnlineStatus(member, group, myGroupMembers);
        int onlineCount = getOnlineCount(group, myGroupMembers);

        // TODO 파라미터가 너무 많음 -> 리팩토링 필요
        return MyGroupDetailResponseDto.of(group, groupTagResponseDtos, isLeader, onlineStatus, onlineCount);
    }

    public List<MyGroupGetMemberResponseDto> getGroupMembersForMember(Long memberId, Long groupId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Group group = groupRepository.findWithGroupMemberById(groupId)
                .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));

        Boolean isJoined = groupMemberQueryRepository.existByMemberIdAndGroupId(member.getId(), groupId);
        if (!isJoined) {
            throw new PlanusException(NOT_JOINED_GROUP);
        }

        List<GroupMember> groupMembers = groupMemberRepository.findAllWithMemberByGroupAndStatus(group);
        List<MyGroupGetMemberResponseDto> responseDtos = groupMembers.stream()
                .map(gm -> MyGroupGetMemberResponseDto.of(gm.getMember(), gm.isLeader(), gm.isOnlineStatus()))
                .collect(Collectors.toList());

        return responseDtos;
    }

    public MemberResponseDto getGroupMemberDetail(Long loginId, Long groupId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Boolean isLoginMemberJoined = groupMemberQueryRepository.existByMemberIdAndGroupId(loginId, groupId);
        Boolean isMemberJoined = groupMemberQueryRepository.existByMemberIdAndGroupId(memberId, groupId);

        if (!isLoginMemberJoined || !isMemberJoined) {
            throw new PlanusException(NOT_JOINED_GROUP);
        }

        return MemberResponseDto.of(member);
    }

    public List<TodoDetailsResponseDto> getGroupMemberPeriodTodos(Long loginId, Long groupId, Long memberId,
                                                                 LocalDate from, LocalDate to) {
        Boolean isLoginMemberJoined = groupMemberQueryRepository.existByMemberIdAndGroupId(loginId, groupId);
        Boolean isMemberJoined = groupMemberQueryRepository.existByMemberIdAndGroupId(memberId, groupId);

        if (!isLoginMemberJoined || !isMemberJoined) {
            throw new PlanusException(NOT_JOINED_GROUP);
        }

        Validator.validateStartDateBeforeEndDate(from, to);
        List<MemberTodo> todos = todoQueryRepository.findPeriodGroupMemberTodosByDate(memberId, groupId, from, to);
        List<TodoDetailsResponseDto> responseDtos = todos.stream()
                .map(TodoDetailsResponseDto::of)
                .collect(Collectors.toList());
        return responseDtos;
    }

    public TodoDailyResponseDto getGroupMemberDailyTodos(Long loginId, Long groupId, Long memberId, LocalDate date) {
        Boolean isLoginMemberJoined = groupMemberQueryRepository.existByMemberIdAndGroupId(loginId, groupId);
        Boolean isMemberJoined = groupMemberQueryRepository.existByMemberIdAndGroupId(memberId, groupId);

        if (!isLoginMemberJoined || !isMemberJoined) {
            throw new PlanusException(NOT_JOINED_GROUP);
        }

        List<MemberTodo> todos = todoQueryRepository.findDailyGroupMemberTodosByDate(memberId, groupId, date);

        // TODO -> 리팩토링 필요. TodoCalendarService에서 구현된 메서드
        List<TodoDailyScheduleDto> dailySchedules = todos.stream()
                .filter(todo -> todo.getStartTime() != null)
                .map(TodoDailyScheduleDto::of)
                .collect(Collectors.toList());

        List<TodoDailyDto> dailyTodos = todos.stream()
                .filter(todo -> todo.getStartTime() == null)
                .map(TodoDailyDto::of)
                .collect(Collectors.toList());

        return TodoDailyResponseDto.of(dailySchedules, dailyTodos);
    }

    @Transactional
    public MyGroupOnlineStatusResponseDto changeOnlineStatus(Long memberId, Long groupId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(member.getId(), groupId)
                .orElseThrow(() -> new PlanusException(NOT_JOINED_GROUP));

        groupMember.changeOnlineStatus();

        return MyGroupOnlineStatusResponseDto.of(groupMember);
    }

    private Boolean isOnlineStatus(Member member, Group group, List<GroupMember> myGroupMembers) {
        return myGroupMembers.stream()
                .filter(groupMember ->
                    groupMember.getGroup().equals(group) && groupMember.getMember().equals(member))
                .map(GroupMember::isOnlineStatus)
                .findFirst().orElseThrow(() -> new PlanusException(INTERNAL_SERVER_ERROR));
    }

    private Boolean isGroupLeader(Member member, List<GroupMember> myGroupMembers) {
        return myGroupMembers.stream().filter(groupMember -> groupMember.getMember().getId().equals(member.getId()))
                .map(GroupMember::isLeader)
                .findFirst().orElseThrow(() -> new PlanusException(INTERNAL_SERVER_ERROR));
    }

    private List<GroupTagResponseDto> getEachGroupTags(Group group, List<GroupTag> allGroupTags) {
        return allGroupTags.stream()
                .filter(groupTag -> groupTag.getGroup().getId().equals(group.getId()))
                .map(GroupTagResponseDto::of)
                .collect(Collectors.toList());
    }

    private int getOnlineCount(Group group, List<GroupMember> allGroupMembers) {
        return (int) allGroupMembers.stream()
                .filter(groupMember -> groupMember.getGroup().getId().equals(group.getId()))
                .filter(GroupMember::isOnlineStatus)
                .count();
    }

    public List<TodoCategoryGetResponseDto> findAllTargetMemberTodoCategories(Long loginMemberId, Long groupId, Long memberId ) {
        Member loginMember = memberRepository.findById( loginMemberId )
                .orElseThrow(() -> new PlanusException( NONE_USER ));

        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP ));

        Member targetMember = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException( NONE_USER ));

        // Member 가 그룹 회원인지 확인
        Boolean isMemberJoined = groupMemberQueryRepository.existByMemberIdAndGroupId( loginMember.getId(), group.getId() );
        if (!isMemberJoined) {
            throw new PlanusException( NOT_JOINED_GROUP );
        }

        // TargetMember 가 그룹 회원인지 확인
        Boolean isTargetMemberJoined = groupMemberQueryRepository.existByMemberIdAndGroupId( targetMember.getId(), group.getId() );
        if (!isTargetMemberJoined) {
            throw new PlanusException( NOT_JOINED_MEMBER_IN_GROUP );
        }

        List<MemberTodoCategory> targetMemberTodoCategories = todoCategoryRepository.findMemberTodoCategoryAllByMember( targetMember );

        // TODO : 그룹 개인 투두 용으로만 쓴 카테고리 뿐 만 아니라 모두 응답으로 주는 것에 대한 보안 문제
        return targetMemberTodoCategories.stream()
                .map( TodoCategoryGetResponseDto::of )
                .collect( Collectors.toList() );
    }
}
