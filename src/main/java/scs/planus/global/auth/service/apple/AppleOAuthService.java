package scs.planus.global.auth.service.apple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.entity.Role;
import scs.planus.domain.member.entity.SocialType;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.global.auth.dto.OAuthLoginResponseDto;
import scs.planus.global.auth.dto.apple.AppleAuthRequestDto;
import scs.planus.global.auth.dto.apple.AppleClientSecretResponseDto;
import scs.planus.global.auth.dto.apple.FullName;
import scs.planus.global.auth.entity.Token;
import scs.planus.global.auth.service.JwtProvider;
import scs.planus.global.exception.PlanusException;
import scs.planus.infra.redis.RedisService;

import javax.transaction.Transactional;

import static scs.planus.global.exception.CustomExceptionStatus.ALREADY_EXIST_SOCIAL_ACCOUNT;
import static scs.planus.global.exception.CustomExceptionStatus.INVALID_USER_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleOAuthService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    private final AppleOAuthUserProvider appleOAuthUserProvider;
    private final AppleJwtProvider appleJwtProvider;

    @Transactional
    public OAuthLoginResponseDto login(AppleAuthRequestDto appleAuthRequestDto) {
        String email = appleOAuthUserProvider.getAppleEmail(appleAuthRequestDto.getIdentityToken());

        Member member  = memberRepository.findByEmail(email)
                .map(this::validateMember)
                .orElseGet(() -> saveNewMember(appleAuthRequestDto.getFullName(), email));

        Token token = jwtProvider.generateToken(member.getEmail());
        redisService.saveValue(member.getEmail(), token);

        return OAuthLoginResponseDto.builder()
                .memberId(member.getId())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
    }

    public AppleClientSecretResponseDto getClientSecret() {
        String clientSecret = appleJwtProvider.createClientSecret();

        return AppleClientSecretResponseDto.builder()
                .clientSecret(clientSecret)
                .build();
    }

    private Member saveNewMember(FullName fullName, String email) {
        String nickName = getName(fullName);
        return memberRepository.save(
                Member.builder()
                        .nickname(nickName)
                        .email(email)
                        .socialType(SocialType.APPLE)
                        .status(Status.ACTIVE)
                        .role(Role.USER)
                        .build()
        );
    }

    private String getName(FullName fullName) {
        if (fullName == null) {
            throw new PlanusException(INVALID_USER_NAME);
        }
        return fullName.getFamilyName() + fullName.getGivenName();
    }

    private Member validateMember(Member member) {
        if (!member.getSocialType().equals(SocialType.APPLE)) {
            throw new PlanusException(ALREADY_EXIST_SOCIAL_ACCOUNT);
        }

        if (member.getStatus().equals(Status.INACTIVE)) {
            member.init(member.getNickname());
        }
        return member;
    }
}
