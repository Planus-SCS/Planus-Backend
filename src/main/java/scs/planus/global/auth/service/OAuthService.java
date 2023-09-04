package scs.planus.global.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.global.auth.dto.OAuthLoginResponseDto;
import scs.planus.global.auth.entity.MemberProfile;
import scs.planus.global.auth.entity.Token;
import scs.planus.global.auth.service.google.GoogleOAuthUserProvider;
import scs.planus.global.auth.service.kakao.KakaoOAuthUserProvider;
import scs.planus.global.exception.PlanusException;
import scs.planus.infra.redis.RedisService;

import static scs.planus.global.exception.CustomExceptionStatus.ALREADY_EXIST_SOCIAL_ACCOUNT;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final KakaoOAuthUserProvider kakaoOAuthUserProvider;
    private final GoogleOAuthUserProvider googleOAuthUserProvider;

    public OAuthLoginResponseDto kakaoLogin(String code) {
        MemberProfile profile = kakaoOAuthUserProvider.getUserInfo(code);
        Member member = saveOrGetExistedMember(profile);
        Token token = jwtProvider.generateToken(member.getEmail());
        redisService.saveValue(member.getEmail(), token);

        return OAuthLoginResponseDto.of(member, token);
    }

    public OAuthLoginResponseDto googleLogin(String code) {
        MemberProfile profile = googleOAuthUserProvider.getUserInfo(code);
        Member member = saveOrGetExistedMember(profile);
        Token token = jwtProvider.generateToken(member.getEmail());
        redisService.saveValue(member.getEmail(), token);

        return OAuthLoginResponseDto.of(member, token);
    }

    private Member saveOrGetExistedMember(MemberProfile profile) {
        Member member = memberRepository.findByEmail(profile.getEmail())
                .map(findMember -> {
                    validateDuplicatedEmail(findMember, profile);
                    return getExistedMember(findMember, profile);
                })
                .orElseGet(() -> memberRepository.save(profile.toEntity()));
        return member;
    }

    private void validateDuplicatedEmail(Member member, MemberProfile profile) {
        if (!member.getSocialType().equals(profile.getSocialType())) {
            throw new PlanusException(ALREADY_EXIST_SOCIAL_ACCOUNT);
        }
    }

    private Member getExistedMember(Member member, MemberProfile profile) {
        if (member.getStatus().equals(Status.INACTIVE)) {
            member.init(profile.getNickname());
        }
        return member;
    }
}
