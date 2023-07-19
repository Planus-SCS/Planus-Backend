package scs.planus.domain.group.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;

import static org.assertj.core.api.Assertions.assertThat;

class GroupMemberTest {

    @DisplayName("빌더를 통해 제대로 GroupMember가 생성되어야 한다.")
    @Test
    void GroupMemberBuilder(){
        //given
        Group group = Group.builder().status(Status.ACTIVE).build();

        //when
        GroupMember groupMember = GroupMember.builder().group(group).build();

        //then
        assertThat(groupMember.isOnlineStatus()).isFalse();
        assertThat(groupMember.getGroup()).isEqualTo(group);
        assertThat(group.getGroupMembers().size()).isEqualTo(1);
    }

    @DisplayName("GroupLeader 생성 메서드 호출 시, Leader와 Authority가 ture여야 한다.")
    @Test
    void createGroupLeader(){
        //given
        Member member = Member.builder().status(Status.ACTIVE).build();
        Group group = Group.builder().status(Status.ACTIVE).build();

        //when
        GroupMember groupMember = GroupMember.createGroupLeader(member, group);

        //then
        assertThat(groupMember.isLeader()).isTrue();
        assertThat(groupMember.isTodoAuthority()).isTrue();
    }

    @DisplayName("GroupMember 생성 메서드 호출 시, Leader와 Authority가 false여야 한다.")
    @Test
    void createGroupMember(){
        //given
        Member member = Member.builder().status(Status.ACTIVE).build();
        Group group = Group.builder().status(Status.ACTIVE).build();

        //when
        GroupMember groupMember = GroupMember.createGroupMember(member, group);

        //then
        assertThat(groupMember.isLeader()).isFalse();
        assertThat(groupMember.isTodoAuthority()).isFalse();
    }

    @DisplayName("onlineStatus가 제대로 변경되어야 한다.")
    @Test
    void changeOnlineStatus(){
        //given
        Member member = Member.builder().status(Status.ACTIVE).build();
        Group group = Group.builder().status(Status.ACTIVE).build();
        GroupMember groupMember = GroupMember.builder().build();

        //when
        groupMember.changeOnlineStatus();

        //then
        assertThat(groupMember.isOnlineStatus()).isTrue();
    }

    @DisplayName("GroupMember의 status가 INACTIVE로 변경되어야 한다.")
    @Test
    void changeStatusToInactive(){
        //given
        Member member = Member.builder().status(Status.ACTIVE).build();
        Group group = Group.builder().status(Status.ACTIVE).build();
        GroupMember groupMember = GroupMember.createGroupMember(member, group);

        //when
        groupMember.changeStatusToInactive();

        //then
        assertThat(groupMember.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @DisplayName("GroupMember의 status가 ACTIVE로 변경되어야 한다.")
    @Test
    void changeStatusToActive(){
        //given
        Member member = Member.builder().status(Status.ACTIVE).build();
        Group group = Group.builder().status(Status.ACTIVE).build();
        GroupMember groupMember = GroupMember.createGroupMember(member, group);

        //when
        groupMember.changeStatusToInactive();
        groupMember.changeStatusToActive();

        //then
        assertThat(groupMember.getStatus()).isEqualTo(Status.ACTIVE);
    }
}