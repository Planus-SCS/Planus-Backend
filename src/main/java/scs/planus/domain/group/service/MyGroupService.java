package scs.planus.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.group.dto.GroupTagResponseDto;
import scs.planus.domain.group.dto.mygroup.GroupBelongInResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupDetailResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupGetMemberResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupOnlineStatusResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupResponseDto;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.entity.GroupTag;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.group.repository.GroupTagRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
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
    private final GroupTagRepository groupTagRepository;

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
                    Boolean onlineStatus = isOnlineStatus(group, myGroupMembers);
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

        // TODO Query를 group이 아닌, groupId로 한다면 생략가능한 코드 코드 -> 나중에 엔티티를 id로 삭 바꿀 필요 존재
        Group group = groupMember.getGroup();

        List<GroupMember> myGroupMembers = groupMemberRepository.findAllWithMemberByGroupAndStatus(group);
        List<GroupTag> groupTags = groupTagRepository.findAllByGroup(group);

        List<GroupTagResponseDto> groupTagResponseDtos = groupTags.stream()
                .map(GroupTagResponseDto::of)
                .collect(Collectors.toList());

        int onlineCount = getOnlineCount(group, myGroupMembers);

        return MyGroupDetailResponseDto.of(group, groupMember, onlineCount, groupTagResponseDtos);
    }

    public List<MyGroupGetMemberResponseDto> getGroupMembers(Long memberId, Long groupId) {
        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        List<GroupMember> groupMembers = groupMemberRepository.findAllWithMemberByGroupAndStatus(groupMember.getGroup());
        List<MyGroupGetMemberResponseDto> responseDtos = groupMembers.stream()
                .map(MyGroupGetMemberResponseDto::of)
                .collect(Collectors.toList());

        return responseDtos;
    }

    @Transactional
    public MyGroupOnlineStatusResponseDto changeOnlineStatus(Long memberId, Long groupId) {
        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        groupMember.changeOnlineStatus();

        return MyGroupOnlineStatusResponseDto.of(groupMember);
    }

    private List<GroupTagResponseDto> getEachGroupTags(Group group, List<GroupTag> allGroupTags) {
        return allGroupTags.stream()
                .filter(groupTag -> groupTag.getGroup().equals(group))
                .map(GroupTagResponseDto::of)
                .collect(Collectors.toList());
    }

    private Boolean isOnlineStatus(Group group, List<GroupMember> myGroupMembers) {
        return myGroupMembers.stream()
                .filter(groupMember -> groupMember.getGroup().equals(group))
                .map(GroupMember::isOnlineStatus)
                .findFirst().orElseThrow(() -> new PlanusException(INTERNAL_SERVER_ERROR));
    }

    private int getOnlineCount(Group group, List<GroupMember> allGroupMembers) {
        return (int) allGroupMembers.stream()
                .filter(groupMember -> groupMember.getGroup().equals(group))
                .filter(GroupMember::isOnlineStatus)
                .count();
    }
}
