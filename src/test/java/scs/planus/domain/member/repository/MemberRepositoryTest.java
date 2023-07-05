package scs.planus.domain.member.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.entity.Role;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    public void init() {
        member = Member.builder()
                .nickname("nickname")
                .email("test@test")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
    }

    @DisplayName("이메일을 통해 멤버를 잘 조회할 수 있어야 한다.")
    @Test
    void findByEmail_Exist() {
        //when
        memberRepository.save(member);
        Member findMember = memberRepository.findByEmail(member.getEmail()).orElse(null);

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember).isEqualTo(member);
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getEmail()).isEqualTo(member.getEmail());
    }

    @DisplayName("이메일이 존재하지 않는 경우에는 Null을 반환한다.")
    @Test
    void findByEmail_Null() {
        //when
        memberRepository.save(member);
        Member findMember = memberRepository.findByEmail("new@error").orElse(null);

        //then
        assertThat(findMember).isNull();
    }
}