package scs.planus.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.group.dto.GroupTagResponseDto;
import scs.planus.domain.group.dto.mygroup.GroupBelongInResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupOnlineStatusResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupResponseDto;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.entity.GroupTag;
import scs.planus.domain.group.repository.GroupMemberRepository;
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

    public List<MyGroupResponseDto> getMyGroups(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        List<GroupMember> myGroupMembers = groupMemberRepository.findAllByActiveGroupAndMemberId(member.getId());
        List<MyGroupResponseDto> responseDtos = getMyGroupResponseDtos(myGroupMembers);

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

    private List<MyGroupResponseDto> getMyGroupResponseDtos(List<GroupMember> myGroupMembers) {
        List<Group> myGroups = myGroupMembers.stream()
                .map(GroupMember::getGroup)
                .collect(Collectors.toList());

        List<GroupMember> allGroupMembers = groupMemberRepository.findAllGroupMemberInGroups(myGroups);
        List<GroupTag> allGroupTags = groupTagRepository.findAllTagInGroups(myGroups);

        List<MyGroupResponseDto> responseDtos = myGroups.stream().map(group -> {
                    List<GroupTagResponseDto> eachGroupTagDtos = allGroupTags.stream()
                            .filter(groupTag -> groupTag.getGroup().getId().equals(group.getId()))
                            .map(GroupTagResponseDto::of)
                            .collect(Collectors.toList());

                    Boolean onlineStatus = myGroupMembers.stream()
                            .filter(groupMember -> groupMember.getGroup().getId().equals(group.getId()))
                            .map(GroupMember::isOnlineStatus)
                            .findFirst().orElseThrow(() -> new PlanusException(INTERNAL_SERVER_ERROR));

                    long onlineCount = allGroupMembers.stream()
                            .filter(groupMember -> groupMember.getGroup().getId().equals(group.getId()))
                            .filter(GroupMember::isOnlineStatus)
                            .count();

                    return MyGroupResponseDto.of(group, eachGroupTagDtos, onlineStatus, onlineCount);
                })
                .collect(Collectors.toList());

        return responseDtos;
    }
}
