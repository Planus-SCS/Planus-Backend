package scs.planus.domain.member.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.domain.Status;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    private Member member;

    @BeforeEach
    void init() {
        member = Member.builder()
                .nickname("test1")
                .description("test test")
                .profileImageUrl("test.jpg")
                .build();
    }

    @Test
    @DisplayName("멤버 프로필 업데이트가 제대로 진행되어야 한다.")
    void updateProfileTest() {
        //when
        member.updateProfile("newNick", "new description", "new.png");

        //then
        assertThat(member.getNickname()).isEqualTo("newNick");
        assertThat(member.getDescription()).isEqualTo("new description");
        assertThat(member.getProfileImageUrl()).isEqualTo("new.png");
    }

    @Test
    @DisplayName("멤버 탈퇴시, status가 제대로 INACTIVE로 변경되어야 한다.")
    void changeStatusToInactiveTest() {
        //when
        member.changeStatusToInactive();

        //then
        assertThat(member.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    @DisplayName("멤버 탈퇴 후, 재가입 시, 제대로 초기화되어야 한다.")
    void initMemberTest() {
        //when
        member.init("newNick");

        //then
        assertThat(member.getNickname()).isEqualTo("newNick");
        assertThat(member.getDescription()).isNull();
        assertThat(member.getProfileImageUrl()).isNull();
        assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);
    }
}