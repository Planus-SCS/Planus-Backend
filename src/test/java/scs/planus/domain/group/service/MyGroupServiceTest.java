package scs.planus.domain.group.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.Status;
import scs.planus.domain.group.dto.mygroup.GroupBelongInResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupDetailResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupGetMemberResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupOnlineStatusResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupResponseDto;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.group.repository.GroupTagRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.support.ServiceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MyGroupServiceTest extends ServiceTest {

    private static final int COUNT = 7;

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupTagRepository groupTagRepository;

    private final MyGroupService myGroupService;

    private Member member;
    private Group group;

    @Autowired
    public MyGroupServiceTest(MemberRepository memberRepository, GroupRepository groupRepository,
                              GroupMemberRepository groupMemberRepository, GroupTagRepository groupTagRepository) {
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupTagRepository = groupTagRepository;

        myGroupService = new MyGroupService(
                memberRepository,
                groupRepository,
                groupMemberRepository,
                groupTagRepository);
    }

    @BeforeEach
    void init() {
        member = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(member);

        group = Group.builder().status(Status.ACTIVE).build();
        groupRepository.save(group);
    }


    @DisplayName("가입한 모든 그룹들을 간략하게 조회할 수 있어야 한다.")
    @Test
    void getMyGroupsInDropDown() {
        //given
        for (int i = 0; i < COUNT; i++) {
            Group group = Group.builder().status(Status.ACTIVE).build();
            groupRepository.save(group);
            groupMemberRepository.save(GroupMember.createGroupMember(member, group));
        }

        //when
        List<GroupBelongInResponseDto> myGroupsInDropDown
                = myGroupService.getMyGroupsInDropDown(member.getId());

        //then
        assertThat(myGroupsInDropDown).hasSize(COUNT);
    }

    @DisplayName("가입한 모든 그룹들을 조회할 수 있어야 한다.")
    @Test
    void getMyAllGroups() {
        //given
        Member groupLeader = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(groupLeader);

        for (int i = 0; i < COUNT; i++) {
            Group group = Group.builder().status(Status.ACTIVE).build();
            groupRepository.save(group);
            groupMemberRepository.save(GroupMember.createGroupLeader(groupLeader, group));
            groupMemberRepository.save(GroupMember.createGroupMember(member, group));
        }

        //when
        List<MyGroupResponseDto> myAllGroups
                = myGroupService.getMyAllGroups(member.getId());

        //then
        assertThat(myAllGroups).hasSize(COUNT);
        assertThat(myAllGroups.get(0).getMemberCount()).isEqualTo(2);
    }

    @DisplayName("해당 그룹의 leader인 경우, Leader와 Authority가 True여야 한다.")
    @Test
    void getMyEachGroupDetail_If_Leader_Then_Leader_And_Authority_Are_True() {
        groupMemberRepository.save(GroupMember.createGroupLeader(member, group));

        //when
        MyGroupDetailResponseDto myEachGroupDetail
                = myGroupService.getMyEachGroupDetail(member.getId(), group.getId());

        //then
        assertThat(myEachGroupDetail.getGroupId()).isEqualTo(group.getId());
        assertThat(myEachGroupDetail.getIsLeader()).isTrue();
        assertThat(myEachGroupDetail.getHasTodoAuthority()).isTrue();
    }

    @DisplayName("해당 그룹의 leader가 아닌 경우, Leader와 Authority가 false여야 한다.")
    @Test
    void getMyEachGroupDetail_If_Not_Leader_Then_Leader_And_Authority_Are_False() {
        Member groupLeader = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(groupLeader);

        groupMemberRepository.save(GroupMember.createGroupLeader(groupLeader, group));
        groupMemberRepository.save(GroupMember.createGroupMember(member, group));

        //when
        MyGroupDetailResponseDto myEachGroupDetail
                = myGroupService.getMyEachGroupDetail(member.getId(), group.getId());

        //then
        assertThat(myEachGroupDetail.getGroupId()).isEqualTo(group.getId());
        assertThat(myEachGroupDetail.getIsLeader()).isFalse();
        assertThat(myEachGroupDetail.getHasTodoAuthority()).isFalse();
    }

    @DisplayName("Group에 속한 GroupMember들이 조회되어야 한다.")
    @Test
    void getGroupMembersForMember() {
        //given
        Member groupLeader = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(groupLeader);

        groupMemberRepository.save(GroupMember.createGroupMember(groupLeader, group));
        groupMemberRepository.save(GroupMember.createGroupMember(member, group));

        //when
        List<MyGroupGetMemberResponseDto> groupMembers
                = myGroupService.getGroupMembers(member.getId(), group.getId());

        //then
        assertThat(groupMembers).hasSize(2);
    }

    @DisplayName("OnlineStatus가 제대로 변경되어야 한다.")
    @Test
    void changeOnlineStatus() {
        //given
        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMemberRepository.save(groupMember);

        //when
        MyGroupOnlineStatusResponseDto myGroupOnlineStatusResponseDto
                = myGroupService.changeOnlineStatus(member.getId(), group.getId());

        //then
        assertThat(myGroupOnlineStatusResponseDto.getGroupMemberId()).isEqualTo(groupMember.getId());
        assertThat(groupMember.isOnlineStatus()).isTrue();
    }
}