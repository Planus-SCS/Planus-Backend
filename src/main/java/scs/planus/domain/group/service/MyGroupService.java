package scs.planus.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.group.dto.GroupTagResponseDto;
import scs.planus.domain.group.dto.mygroup.GroupBelongInResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupDetailResponseDto;
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
                    List<GroupTagResponseDto> eachGroupTagDtos = getEachGroupTags(allGroupTags, group);
                    Boolean onlineStatus = isOnlineStatus(myGroupMembers, member);
                    int onlineCount = getOnlineCount(allGroupMembers, group);

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

        Boolean onlineStatus = isOnlineStatus(myGroupMembers, member);
        int onlineCount = getOnlineCount(myGroupMembers, group);

        return MyGroupDetailResponseDto.of(group,groupTagResponseDtos, onlineStatus, onlineCount);
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

    private List<GroupTagResponseDto> getEachGroupTags(List<GroupTag> allGroupTags, Group group) {
        return allGroupTags.stream()
                .filter(groupTag -> groupTag.getGroup().getId().equals(group.getId()))
                .map(GroupTagResponseDto::of)
                .collect(Collectors.toList());
    }

    private Boolean isOnlineStatus(List<GroupMember> myGroupMembers, Member member) {
        return myGroupMembers.stream()
                .filter(groupMember -> groupMember.getMember().getId().equals(member.getId()))
                .map(GroupMember::isOnlineStatus)
                .findFirst().orElseThrow(() -> new PlanusException(INTERNAL_SERVER_ERROR));
    }

    private int getOnlineCount(List<GroupMember> allGroupMembers, Group group) {
        return (int) allGroupMembers.stream()
                .filter(groupMember -> groupMember.getGroup().getId().equals(group.getId()))
                .filter(GroupMember::isOnlineStatus)
                .count();
    }
}
