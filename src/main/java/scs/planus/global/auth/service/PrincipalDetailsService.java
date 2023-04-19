package scs.planus.global.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.exception.PlanusException;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;

import static scs.planus.global.exception.CustomExceptionStatus.NONE_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public PrincipalDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new PlanusException(NONE_USER));
        log.info("loadUserByUsername, member=[{}][{}][{}]", member.getId(),member.getEmail(), member.getRole());
        return new PrincipalDetails(member);
    }
}
