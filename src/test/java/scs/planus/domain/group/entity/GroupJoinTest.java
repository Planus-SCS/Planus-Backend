package scs.planus.domain.group.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;

import static org.assertj.core.api.Assertions.assertThat;

class GroupJoinTest {
    private Member member;
    private Group group;

    @BeforeEach
    void init() {
        member = Member.builder()
                .build();

        group = Group.builder()
                .build();
    }

    @Test
    void createGroupJoin() {
        // when
        GroupJoin groupJoin = GroupJoin.createGroupJoin(member, group);

        // then
        assertThat(groupJoin.getMember()).isEqualTo(member);
        assertThat(groupJoin.getGroup()).isEqualTo(group);
        assertThat(groupJoin.getStatus()).isEqualTo(Status.INACTIVE);
    }
}