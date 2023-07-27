package scs.planus.domain.group.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.Status;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.support.RepositoryTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GroupMemberRepositoryTest extends RepositoryTest {

    private static final int COUNT = 7;

    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupRepository groupRepository;

    private Member member;
    private Group group;

    @BeforeEach
    void init() {
        member = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(member);

        group = Group.builder().status(Status.ACTIVE).build();
        groupRepository.save(group);
    }

    @DisplayName("memberId와 groupId를 통해 status가 active인 GroupMember가 조회되어야 한다.")
    @Test
    void findByMemberIdAndGroupId() {
        //given
        groupMemberRepository.save(GroupMember.builder()
                .member(member)
                .group(group)
                .build());

        //when
        GroupMember groupMember
                = groupMemberRepository.findByMemberIdAndGroupId(member.getId(), group.getId()).orElse(null);

        //then
        assertThat(groupMember.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(groupMember.getMember()).isEqualTo(member);
        assertThat(groupMember.getGroup()).isEqualTo(group);
    }

    @DisplayName("createGroupMember 호출시, GroupMember가 저장되어야 한다.")
    @Test
    void createGroupMember_Then_Save_GroupMember() {
        //given
        GroupMember groupMember = GroupMember.createGroupMember(member, group);

        //when
        GroupMember findGroupMember
                = groupMemberRepository.findByMemberIdAndGroupId(member.getId(), group.getId()).orElse(null);

        //then
        assertThat(findGroupMember).isNotNull();
        assertThat(findGroupMember).isEqualTo(groupMember);
        assertThat(findGroupMember.isLeader()).isFalse();
        assertThat(findGroupMember.isTodoAuthority()).isFalse();
    }

    @DisplayName("createGroupLeader 호출시, GroupMember(leader)가 저장되어야 한다.")
    @Test
    void createGroupLeader_Then_Save_GroupLeader() {
        //given
        GroupMember groupMember = GroupMember.createGroupLeader(member, group);

        //when
        GroupMember findGroupMember
                = groupMemberRepository.findByMemberIdAndGroupId(member.getId(), group.getId()).orElse(null);

        //then
        assertThat(findGroupMember).isNotNull();
        assertThat(findGroupMember).isEqualTo(groupMember);
        assertThat(findGroupMember.isLeader()).isTrue();
        assertThat(findGroupMember.isTodoAuthority()).isTrue();
    }

    @DisplayName("group을 이용하여 해당 그룹의 leader가 조회되어야 한다.")
    @Test
    void findWithGroupAndLeaderByGroup() {
        //given
        GroupMember.createGroupLeader(member, group);

        //when
        GroupMember groupMember
                = groupMemberRepository.findWithGroupAndLeaderByGroup(group).orElse(null);

        //then
        assertThat(groupMember.isLeader()).isTrue();
    }

    @DisplayName("memberId와 groupId를 통해 status가 inactive인 GroupMember가 조회되어야 한다.")
    @Test
    void findByMemberIdAndGroupIdAndInactive() {
        //given
        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMember.changeStatusToInactive();

        //when
        GroupMember findGroupMember
                = groupMemberRepository.findByMemberIdAndGroupIdAndInactive(member.getId(), group.getId()).orElse(null);

        //then
        assertThat(findGroupMember.getStatus()).isEqualTo(Status.INACTIVE);
        assertThat(findGroupMember.getMember()).isEqualTo(member);
        assertThat(findGroupMember.getGroup()).isEqualTo(group);
    }

    @DisplayName("group을 통해 해당 그룹에 속한 모든 GroupMember가 조회되어야 한다.")
    @Test
    void findAllWithMemberByGroupAndStatus() {
        Member groupLeader = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(groupLeader);

        GroupMember.createGroupMember(member, group);
        GroupMember.createGroupLeader(groupLeader, group);

        //when
        List<GroupMember> groupMembers
                = groupMemberRepository.findAllWithMemberByGroupAndStatus(group);

        //then
        assertThat(groupMembers.size()).isEqualTo(2);
        assertThat(groupMembers.get(0).isLeader()).isTrue();
    }

    @DisplayName("해당 멤버가 포함된 모든 GroupMember가 조회되어야 한다.")
    @Test
    void findAllByActiveGroupAndMemberId() {
        //given
        for (int i = 0; i < COUNT; i++) {
            Group group = Group.builder().status(Status.ACTIVE).build();
            groupRepository.save(group);

            GroupMember.createGroupMember(member, group);
        }

        //when
        List<GroupMember> groupMembers
                = groupMemberRepository.findAllByActiveGroupAndMemberId(member.getId());

        //then
        assertThat(groupMembers.size()).isEqualTo(COUNT);
    }

    @DisplayName("해당 멤버가 속한 Group가 Inactive라면 해당 멤버에 대한 GroupMember가 조회되서는 안된다.")
    @Test
    void findAllByActiveGroupAndMemberId_Not_Select_If_Group_Status_Is_Inactive() {
        //given
        Group group = Group.builder().status(Status.INACTIVE).build();
        groupRepository.save(group);

        GroupMember.createGroupMember(member, group);

        //when
        List<GroupMember> groupMembers
                = groupMemberRepository.findAllByActiveGroupAndMemberId(member.getId());

        //then
        assertThat(groupMembers).isEmpty();
    }

    @DisplayName("해당 멤버가 포함된 GroupMember가 Inactive라면 해당 멤버에 대한 GroupMember가 조회되서는 안된다.")
    @Test
    void findAllByActiveGroupAndMemberId_Not_Select_If_GroupMember_Status_Is_Inactive() {
        //given
        Group group = Group.builder().status(Status.INACTIVE).build();
        groupRepository.save(group);

        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMember.changeStatusToInactive();

        //when
        List<GroupMember> groupMembers
                = groupMemberRepository.findAllByActiveGroupAndMemberId(member.getId());

        //then
        assertThat(groupMembers).isEmpty();
    }

    @DisplayName("List 타입의 Groups에 속하는 모든 GroupMember가 조회되어야 한다.")
    @Test
    void findAllGroupMemberInGroups() {
        //given
        Group group2 = Group.builder().status(Status.ACTIVE).build();
        groupRepository.save(group2);

        GroupMember.createGroupMember(member, group);
        GroupMember.createGroupMember(member, group2);

        for (int i = 0; i < COUNT - 1; i++) {
            Member anotherMember = Member.builder().status(Status.ACTIVE).build();
            memberRepository.save(anotherMember);

            GroupMember.createGroupMember(anotherMember, group);
            GroupMember.createGroupMember(anotherMember, group2);
        }
        List<Group> groups = List.of(group, group2);

        //when
        List<GroupMember> groupMembers
                = groupMemberRepository.findAllGroupMemberInGroups(groups);

        //then
        assertThat(groupMembers.size()).isEqualTo(COUNT * 2);
    }

    @DisplayName("해당 멤버가 leader인, 해당 멤버가 포함된 모든 GroupMember들이 조회되어야 한다.")
    @Test
    void findWithGroupByLeaderMember() {
        //given
        Group group2 = Group.builder().status(Status.ACTIVE).build();
        groupRepository.save(group2);

        GroupMember.createGroupLeader(member, group);
        GroupMember.createGroupMember(member, group2);

        //when
        List<GroupMember> groupMembers
                = groupMemberRepository.findWithGroupByLeaderMember(member);

        //then
        assertThat(groupMembers.size()).isEqualTo(1);
    }
}