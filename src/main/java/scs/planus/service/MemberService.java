package scs.planus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.common.exception.PlanusException;
import scs.planus.common.response.CustomResponseStatus;
import scs.planus.domain.Member;
import scs.planus.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    /**
     * 멤버 조회
     */
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    throw new PlanusException(CustomResponseStatus.NONE_USER);
                });
    }
}
