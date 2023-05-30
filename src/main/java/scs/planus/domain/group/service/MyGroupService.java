package scs.planus.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.dto.GroupTagResponseDto;
import scs.planus.domain.group.dto.mygroup.GroupBelongInResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupDetailResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupGetMemberResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupOnlineStatusResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupResponseDto;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.entity.GroupTag;
import scs.planus.domain.group.repository.GroupMemberQueryRepository;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.group.repository.GroupTagRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.global.exception.PlanusException;

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
        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        // TODO Query를 group이 아닌, groupId로 한다면 생략가능한 코드 코드
        Group group = groupMember.getGroup();

        List<GroupMember> myGroupMembers = groupMemberRepository.findAllWithMemberByGroupAndStatus(group);
        List<GroupTag> groupTags = groupTagRepository.findAllByGroup(group);

        List<GroupTagResponseDto> groupTagResponseDtos = groupTags.stream()
                .map(GroupTagResponseDto::of)
                .collect(Collectors.toList());

        int onlineCount = getOnlineCount(group, myGroupMembers);

        return MyGroupDetailResponseDto.of(group, groupMember, onlineCount, groupTagResponseDtos);
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
                .filter(groupMember -> groupMember.getGroup().equals(group))
                .filter(GroupMember::isOnlineStatus)
                .count();
    }
}
