package scs.planus.global.auth.service.apple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.entity.SocialType;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.global.auth.dto.OAuthLoginResponseDto;
import scs.planus.global.auth.dto.apple.AppleAuthRequestDto;
import scs.planus.global.auth.dto.apple.AppleClientSecretResponseDto;
import scs.planus.global.auth.dto.apple.FullName;
import scs.planus.global.auth.entity.Token;
import scs.planus.global.auth.entity.apple.ApplePublicKey;
import scs.planus.global.auth.entity.apple.ApplePublicKeys;
import scs.planus.global.auth.service.JwtProvider;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.util.encryptor.Encryptor;
import scs.planus.infra.redis.RedisService;
import scs.planus.support.ServiceTest;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static scs.planus.global.exception.CustomExceptionStatus.*;

@Import({AppleJwtParser.class,
        AppleAuthClient.class,
        AppleJwtProvider.class,
        ApplePublicKeyGenerator.class,
        AppleClaimsValidator.class})
@Slf4j
class AppleOAuthServiceTest extends ServiceTest {
    private final MemberRepository memberRepository;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private final RedisService redisService;
    @MockBean
    private AppleJwtProvider appleJwtProvider;
    private final AppleJwtParser appleJwtParser;
    @MockBean
    private final AppleAuthClient appleAuthClient;
    @MockBean
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    @MockBean
    private final AppleClaimsValidator appleClaimsValidator;

    private final AppleOAuthService appleOAuthService;

    private AppleAuthRequestDto appleAuthRequestDto;

    private String identityToken;

    @Autowired
    public AppleOAuthServiceTest(MemberRepository memberRepository,
                                 JwtProvider jwtProvider,
                                 RedisService redisService,
                                 AppleJwtParser appleJwtParser,
                                 AppleAuthClient appleAuthClient,
                                 AppleJwtProvider appleJwtProvider,
                                 ApplePublicKeyGenerator applePublicKeyGenerator,
                                 AppleClaimsValidator appleClaimsValidator) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
        this.appleJwtParser = appleJwtParser;
        this.appleAuthClient = appleAuthClient;
        this.appleJwtProvider = appleJwtProvider;
        this.applePublicKeyGenerator = applePublicKeyGenerator;
        this.appleClaimsValidator = appleClaimsValidator;

        appleOAuthService = new AppleOAuthService(
                memberRepository,
                jwtProvider,
                redisService,
                appleJwtParser,
                appleAuthClient,
                appleJwtProvider,
                applePublicKeyGenerator,
                appleClaimsValidator
        );
    }

    @BeforeEach
    void init() {
        Date now = new Date();
        long expirationTime = 24 * 60 * 60 * 1000;

        KeyPair keyPair = generateKeyPair();

        identityToken = Jwts.builder()
                .setHeaderParam("kid", "kid")
                .claim("email", "email")
                .claim("nonce", Encryptor.encryptWithSHA256("nonce"))
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("clientId")
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();

        FullName fullName = FullName.builder()
                .familyName("플")
                .givenName("래너스")
                .build();

        appleAuthRequestDto = AppleAuthRequestDto.builder()
                .identityToken(identityToken)
                .fullName(fullName)
                .build();

        String N = RandomStringUtils.randomAlphanumeric(128);
        String E = RandomStringUtils.randomAlphanumeric(4);
        ApplePublicKeys applePublicKeys = new ApplePublicKeys(
                List.of(new ApplePublicKey("RSA",  "kid", "use", "RS256", N, E))
        );

        given(appleAuthClient.getApplePublicKey()).willReturn(applePublicKeys);
        given(applePublicKeyGenerator.generatePublicKey(anyMap(), any(ApplePublicKeys.class))).willReturn(keyPair.getPublic());
        given(appleClaimsValidator.isValid(any(Claims.class))).willReturn(true);
    }

    @DisplayName("회원가입이 정상적으로 이루어 져야 한다.")
    @Test
    void login_Success_Join() {
        // given
        Token token = Token.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        given(jwtProvider.generateToken(anyString())).willReturn(token);
        willDoNothing().given(redisService).saveValue(anyString(), any(Token.class));

        // when
        OAuthLoginResponseDto oAuthLoginResponseDto = appleOAuthService.login(appleAuthRequestDto);

        // then
        assertThat(oAuthLoginResponseDto).hasNoNullFieldsOrProperties();

        verify(appleAuthClient).getApplePublicKey();
        verify(applePublicKeyGenerator).generatePublicKey(anyMap(), any(ApplePublicKeys.class));
        verify(appleClaimsValidator).isValid(any(Claims.class));
        verify(jwtProvider).generateToken(anyString());
        verify(redisService).saveValue(anyString(), any(Token.class));
    }

    @DisplayName("비활성화 계정일 경우, 재활성화 및 사용자 정보 초기화가 되어야 한다.")
    @Test
    void login_Success_Reactivation() {
        // given
        Member InactiveMember = memberRepository.save(Member.builder()
                .nickname("비활성계정")
                .email("email")
                .socialType(SocialType.APPLE)
                .status(Status.INACTIVE)
                .build());

        Token token = Token.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        given(jwtProvider.generateToken(anyString())).willReturn(token);
        willDoNothing().given(redisService).saveValue(anyString(), any(Token.class));

        // when
        OAuthLoginResponseDto oAuthLoginResponseDto = appleOAuthService.login(appleAuthRequestDto);

        // then
        assertThat(oAuthLoginResponseDto.getMemberId()).isEqualTo(InactiveMember.getId());
        assertThat(oAuthLoginResponseDto).hasNoNullFieldsOrProperties();

        verify(appleAuthClient).getApplePublicKey();
        verify(applePublicKeyGenerator).generatePublicKey(anyMap(), any(ApplePublicKeys.class));
        verify(appleClaimsValidator).isValid(any(Claims.class));
        verify(jwtProvider).generateToken(anyString());
        verify(redisService).saveValue(anyString(), any(Token.class));
    }

    @DisplayName("회원가입 시 FullName 이 null 인 경우," +
                "INVALID_USER_NAME 예외가 발생해야 한다.")
    @Test
    void login_Fail_INVALID_USER_NAME() {
        // given
        appleAuthRequestDto = AppleAuthRequestDto.builder()
                .identityToken(identityToken)
                .build();

        // when & then
        assertThatThrownBy(() -> appleOAuthService.login(appleAuthRequestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_USER_NAME);

        verify(appleAuthClient).getApplePublicKey();
        verify(applePublicKeyGenerator).generatePublicKey(anyMap(), any(ApplePublicKeys.class));
        verify(appleClaimsValidator).isValid(any(Claims.class));
    }

    @DisplayName("identity claims 의 검증 결과가 false 인 경우," +
                "INVALID_APPLE_IDENTITY_TOKEN 예외가 발생해야 한다.")
    @Test
    void login_Fail_INVALID_APPLE_IDENTITY_TOKEN() {
        // given
        given(appleClaimsValidator.isValid(any(Claims.class))).willReturn(false);

        // when & then
        assertThatThrownBy(() -> appleOAuthService.login(appleAuthRequestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_APPLE_IDENTITY_TOKEN);

        verify(appleAuthClient).getApplePublicKey();
        verify(applePublicKeyGenerator).generatePublicKey(anyMap(), any(ApplePublicKeys.class));
        verify(appleClaimsValidator).isValid(any(Claims.class));
    }

    @DisplayName("다른 계정으로 가입된 이메일인 경우," +
                "ALREADY_EXIST_SOCIAL_ACCOUNT 예외를 발생 시킨다.")
    @Test
    void login_Fail_ALREADY_EXIST_SOCIAL_ACCOUNT() {
        // given
        memberRepository.save(Member.builder()
                .email("email")
                .socialType(SocialType.KAKAO)
                .build());

        // when & then
        assertThatThrownBy(() -> appleOAuthService.login(appleAuthRequestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(ALREADY_EXIST_SOCIAL_ACCOUNT);

        verify(appleAuthClient).getApplePublicKey();
        verify(applePublicKeyGenerator).generatePublicKey(anyMap(), any(ApplePublicKeys.class));
        verify(appleClaimsValidator).isValid(any(Claims.class));
    }

    @DisplayName("생성된 clientSecret 토큰을 AppleClientSecretResponseDto 로 반환해야 한다.")
    @Test
    void getClientSecret() {
        // given
        given(appleJwtProvider.createClientSecret()).willReturn("clientSecret");

        // when
        AppleClientSecretResponseDto clientSecret = appleOAuthService.getClientSecret();

        // then
        assertThat(clientSecret).isNotNull();
    }

    private KeyPair generateKeyPair() {
        try {
            return KeyPairGenerator.getInstance("RSA")
                    .generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new PlanusException(NO_SUCH_ALGORITHM);
        }
    }
}