package scs.planus.global.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.exception.PlanusException;
import scs.planus.support.ServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scs.planus.global.exception.CustomExceptionStatus.NONE_USER;

@ServiceTest
class PrincipalDetailsServiceTest {

    private final MemberRepository memberRepository;
    private PrincipalDetailsService principalDetailsService;

    @Autowired
    public PrincipalDetailsServiceTest(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        principalDetailsService = new PrincipalDetailsService(memberRepository);
    }

    @DisplayName("존재하는 이메일의 회원인 경우, 그 회원에 관한 PrincipalDetail을 반환한다.")
    @Test
    void loadUserByUsername(){
        //given
        Member member = Member.builder().email("test@test").build();
        memberRepository.save(member);

        //when
        PrincipalDetails principalDetails
                = principalDetailsService.loadUserByUsername("test@test");

        //then
        assertThat(principalDetails.getId()).isEqualTo(member.getId());
        assertThat(principalDetails.getUsername()).isEqualTo(member.getEmail());
    }

    @DisplayName("존재하지 않는 이메일의 회원인 경우, 예외를 던진다.")
    @Test
    void loadUserByUsername_Throw_Exception_If_Not_Existed_Email(){
        //given
        Member member = Member.builder().email("test@test").build();
        memberRepository.save(member);

        //then
        assertThatThrownBy(() ->
                principalDetailsService.loadUserByUsername("wrong@test"))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NONE_USER);
    }
}