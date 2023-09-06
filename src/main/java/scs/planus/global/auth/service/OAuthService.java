package scs.planus.global.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.global.auth.dto.OAuthLoginResponseDto;
import scs.planus.global.auth.dto.apple.AppleAuthRequestDto;
import scs.planus.global.auth.dto.apple.AppleClientSecretResponseDto;
import scs.planus.global.auth.dto.apple.FullName;
import scs.planus.global.auth.entity.userinfo.AppleUserInfo;
import scs.planus.global.auth.entity.userinfo.OAuthUserInfo;
import scs.planus.global.auth.entity.Token;
import scs.planus.global.auth.service.apple.AppleJwtProvider;
import scs.planus.global.auth.service.apple.AppleOAuthUserProvider;
import scs.planus.global.auth.service.google.GoogleOAuthUserProvider;
import scs.planus.global.auth.service.kakao.KakaoOAuthUserProvider;
import scs.planus.global.exception.PlanusException;
import scs.planus.infra.redis.RedisService;

import static scs.planus.global.exception.CustomExceptionStatus.ALREADY_EXIST_SOCIAL_ACCOUNT;
import static scs.planus.global.exception.CustomExceptionStatus.INVALID_USER_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final KakaoOAuthUserProvider kakaoOAuthUserProvider;
    private final GoogleOAuthUserProvider googleOAuthUserProvider;
    private final AppleOAuthUserProvider appleOAuthUserProvider;
    private final AppleJwtProvider appleJwtProvider;

    public OAuthLoginResponseDto kakaoLogin(String code) {
        OAuthUserInfo kakaoMember = kakaoOAuthUserProvider.getUserInfo(code);
        Member member = saveOrGetExistedMember(kakaoMember);
        Token token = jwtProvider.generateToken(member.getEmail());
        redisService.saveValue(member.getEmail(), token);

        return OAuthLoginResponseDto.of(member, token);
    }

    public OAuthLoginResponseDto googleLogin(String code) {
        OAuthUserInfo googleMember = googleOAuthUserProvider.getUserInfo(code);
        Member member = saveOrGetExistedMember(googleMember);
        Token token = jwtProvider.generateToken(member.getEmail());
        redisService.saveValue(member.getEmail(), token);

        return OAuthLoginResponseDto.of(member, token);
    }

    public OAuthLoginResponseDto appleLogin(AppleAuthRequestDto appleAuthRequestDto) {
        OAuthUserInfo appleMember = appleOAuthUserProvider.getUserInfo(appleAuthRequestDto.getIdentityToken());
        Member member = saveOrGetExistedAppleMember(appleMember, appleAuthRequestDto.getFullName());
        Token token = jwtProvider.generateToken(member.getEmail());
        redisService.saveValue(member.getEmail(), token);

        return OAuthLoginResponseDto.of(member, token);
    }

    public AppleClientSecretResponseDto getClientSecret() {
        String clientSecret = appleJwtProvider.createClientSecret();

        return AppleClientSecretResponseDto.builder()
                .clientSecret(clientSecret)
                .build();
    }

    private Member saveOrGetExistedMember(OAuthUserInfo userInfo) {
        return memberRepository.findByEmail(userInfo.getEmail())
                .map(findMember -> {
                    validateDuplicatedEmail(findMember, userInfo);
                    return getExistedMember(findMember, userInfo);
                })
                .orElseGet(() -> memberRepository.save(userInfo.toMember()));
    }

    private Member saveOrGetExistedAppleMember(OAuthUserInfo userInfo, FullName fullName) {
        return memberRepository.findByEmail(userInfo.getEmail())
                .map(findMember -> {
                    validateDuplicatedEmail(findMember, userInfo);
                    return getExistedMember(findMember, userInfo);
                })
                .orElseGet(() -> {
                    String nickname = getNicknameFromFullName(fullName);
                    AppleUserInfo appleUserInfo = (AppleUserInfo) userInfo;
                    appleUserInfo.updateNickname(nickname);
                    return memberRepository.save(appleUserInfo.toMember());
                });
    }

    private void validateDuplicatedEmail(Member findMember, OAuthUserInfo userInfo) {
        if (!findMember.getSocialType().equals(userInfo.getSocialType())) {
            throw new PlanusException(ALREADY_EXIST_SOCIAL_ACCOUNT);
        }
    }

    private Member getExistedMember(Member findMember, OAuthUserInfo userInfo) {
        if (findMember.getStatus().equals(Status.INACTIVE)) {
            findMember.init(userInfo.getNickname());
        }
        return findMember;
    }

    private String getNicknameFromFullName(FullName fullName) {
        if (fullName == null) {
            throw new PlanusException(INVALID_USER_NAME);
        }
        return fullName.getFamilyName() + fullName.getGivenName();
    }
}
