package scs.planus.domain.group.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.tag.entity.Tag;
import scs.planus.global.exception.PlanusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scs.planus.global.exception.CustomExceptionStatus.NOT_EXIST_LEADER;

class GroupTest {

    @DisplayName("생성메서드로 제대로 생성되어야 한다.")
    @Test
    void createGroup(){
        //given
        String name = "group";
        String notice = "group notice";
        int limitCount = 5;
        String groupImageUrl = "imageUrl";

        //when
        Group group = Group.creatGroup(name, notice, limitCount, groupImageUrl);

        //then
        assertThat(group.getName()).isEqualTo(name);
        assertThat(group.getNotice()).isEqualTo(notice);
        assertThat(group.getLimitCount()).isEqualTo(limitCount);
        assertThat(group.getGroupImageUrl()).isEqualTo(groupImageUrl);
        assertThat(group.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @DisplayName("Group의 leader가 조회되어야 한다.")
    @Test
    void getLeader(){
        //given
        Group group = Group.builder()
                .status(Status.ACTIVE)
                .build();
        Member member = Member.builder()
                .nickname("leader")
                .build();
        GroupMember.builder()
                .leader(true)
                .member(member)
                .group(group)
                .build();

        //when
        Member leader = group.getLeader();

        //then
        assertThat(leader).isEqualTo(member);
        assertThat(leader.getNickname()).isEqualTo(member.getNickname());
    }

    @DisplayName("Group의 leader가 없는 경우 예외를 던진다.")
    @Test
    void getLeader_Throw_Exception_If_Empty_Leader(){
        //given
        Group group = Group.builder()
                .status(Status.ACTIVE)
                .build();

        //then
        assertThatThrownBy(group::getLeader)
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_LEADER);
    }

    @DisplayName("Group의 디테일한 정보 변경이 제대로 이루어져야 한다.")
    @Test
    void updateDetail(){
        //given
        Group group = Group.builder()
                .limitCount(50)
                .groupImageUrl("oldGroupImageUrl")
                .build();

        //when
        group.updateDetail(5, "newGroupImageUrl");

        //then
        assertThat(group.getLimitCount()).isEqualTo(5);
        assertThat(group.getGroupImageUrl()).isEqualTo("newGroupImageUrl");
    }

    @DisplayName("Group의 소개 정보 변경이 제대로 이루어져야 한다.")
    @Test
    void updateNotice(){
        //given
        Group group = Group.builder()
                .notice("oldNotice")
                .build();

        //when
        group.updateNotice("newNotice");

        //then
        assertThat(group.getNotice()).isEqualTo("newNotice");
    }

    @DisplayName("Group의 status가 Inactive로 변경되어야 한다.")
    @Test
    void changeStatusToInactive(){
        //given
        Group group = Group.builder()
                .status(Status.ACTIVE)
                .build();

        //when
        group.changeStatusToInactive();

        //then
        assertThat(group.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @DisplayName("Group의 GroupTag가 삭제되어야 한다.")
    @Test
    void removeGroupTag(){
        //given
        Group group = Group.builder()
                .status(Status.ACTIVE)
                .build();

        GroupTag groupTag = GroupTag.builder()
                .group(group)
                .tag(Tag.builder().build())
                .build();

        //when
        group.removeGroupTag(groupTag);

        //then
        assertThat(group.getGroupTags()).isEmpty();
    }
}