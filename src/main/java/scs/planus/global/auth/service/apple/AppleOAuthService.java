package scs.planus.global.auth.service.apple;

import io.jsonwebtoken.Claims;
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
import scs.planus.global.auth.entity.apple.ApplePublicKeys;
import scs.planus.global.auth.service.JwtProvider;
import scs.planus.global.exception.PlanusException;
import scs.planus.infra.redis.RedisService;

import javax.transaction.Transactional;
import java.security.PublicKey;
import java.util.Map;

import static scs.planus.global.exception.CustomExceptionStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleOAuthService {
    private static final String EMAIL_KEY = "email";

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    private final AppleJwtParser appleJwtParser;
    private final AppleAuthClient appleAuthClient;
    private final AppleJwtProvider appleJwtProvider;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    @Transactional
    public OAuthLoginResponseDto login(AppleAuthRequestDto appleAuthRequestDto) {
        String email = getAppleEmail(appleAuthRequestDto.getIdentityToken());

        Member member  = memberRepository.findByEmail(email)
                .map(this::validateMember)
                .orElseGet(() -> saveNewMember(appleAuthRequestDto, email));

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

    public String getAppleEmail(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);

        ApplePublicKeys applePublicKey = appleAuthClient.getApplePublicKey();

        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, applePublicKey);

        Claims claims = appleJwtParser.parseClaimWithPublicKey(identityToken, publicKey);

        validateClaims(claims);

        return claims.get(EMAIL_KEY, String.class);
    }

    public Member saveNewMember(AppleAuthRequestDto appleAuthRequestDto, String email) {
        String nickName = getName(appleAuthRequestDto.getFullName());
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

    public String getName(FullName fullName) {
        if (fullName == null) {
            throw new PlanusException(INVALID_USER_NAME);
        }
        return fullName.getFamilyName() + fullName.getGivenName();
    }

    public Member validateMember(Member member) {
        if (!member.getSocialType().equals(SocialType.APPLE)) {
            throw new PlanusException(ALREADY_EXIST_SOCIAL_ACCOUNT);
        }

        if (member.getStatus().equals(Status.INACTIVE)) {
            member.init(member.getNickname());
        }
        return member;
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.validation(claims)) {
            throw new PlanusException(INVALID_APPLE_IDENTITY_TOKEN);
        }
    }
}
