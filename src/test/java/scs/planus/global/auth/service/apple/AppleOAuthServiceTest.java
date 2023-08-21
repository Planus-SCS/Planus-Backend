package scs.planus.global.auth.service.apple;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;
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
import scs.planus.support.ServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static scs.planus.global.exception.CustomExceptionStatus.ALREADY_EXIST_SOCIAL_ACCOUNT;
import static scs.planus.global.exception.CustomExceptionStatus.INVALID_USER_NAME;

@Slf4j
class AppleOAuthServiceTest extends ServiceTest {
    private static final String IDENTITY_TOKEN = "identityToken";
    private static final String EMAIL = "email";

    private final MemberRepository memberRepository;
    @MockBean
    private final JwtProvider jwtProvider;
    @MockBean
    private final RedisService redisService;
    @MockBean
    private final AppleOAuthUserProvider appleOAuthUserProvider;
    @MockBean
    private final AppleJwtProvider appleJwtProvider;

    private final AppleOAuthService appleOAuthService;

    private AppleAuthRequestDto appleAuthRequestDto;

    @Autowired
    public AppleOAuthServiceTest(MemberRepository memberRepository,
                                 JwtProvider jwtProvider,
                                 RedisService redisService,
                                 AppleOAuthUserProvider appleOAuthUserProvider,
                                 AppleJwtProvider appleJwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
        this.appleJwtProvider = appleJwtProvider;
        this.appleOAuthUserProvider = appleOAuthUserProvider;

        appleOAuthService = new AppleOAuthService(
                memberRepository,
                jwtProvider,
                redisService,
                appleOAuthUserProvider,
                appleJwtProvider
        );
    }

    @BeforeEach
    void init() {
        FullName fullName = FullName.builder()
                .familyName("성")
                .givenName("이름")
                .build();

        appleAuthRequestDto = AppleAuthRequestDto.builder()
                .identityToken(IDENTITY_TOKEN)
                .fullName(fullName)
                .build();
    }

    @DisplayName("회원가입이 정상적으로 이루어 져야 한다.")
    @Test
    void login_Success_Join() {
        // given
        Token token = Token.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        given(appleOAuthUserProvider.getAppleEmail(anyString())).willReturn(EMAIL);
        given(jwtProvider.generateToken(anyString())).willReturn(token);
        willDoNothing().given(redisService).saveValue(anyString(), any(Token.class));

        // when
        OAuthLoginResponseDto oAuthLoginResponseDto = appleOAuthService.login(appleAuthRequestDto);

        // then
        assertThat(oAuthLoginResponseDto).hasNoNullFieldsOrProperties();

        verify(appleOAuthUserProvider).getAppleEmail(anyString());
        verify(jwtProvider).generateToken(anyString());
        verify(redisService).saveValue(anyString(), any(Token.class));
    }

    @DisplayName("비활성화 계정일 경우, 재활성화 및 사용자 정보 초기화가 되어야 한다.")
    @Test
    void login_Success_Reactivation() {
        // given
        Member InactiveMember = memberRepository.save(
                Member.builder()
                        .nickname("비활성계정")
                        .email(EMAIL)
                        .socialType(SocialType.APPLE)
                        .status(Status.INACTIVE)
                        .build()
        );

        Token token = Token.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        given(appleOAuthUserProvider.getAppleEmail(anyString())).willReturn(EMAIL);
        given(jwtProvider.generateToken(anyString())).willReturn(token);
        willDoNothing().given(redisService).saveValue(anyString(), any(Token.class));

        // when
        OAuthLoginResponseDto oAuthLoginResponseDto = appleOAuthService.login(appleAuthRequestDto);

        // then
        assertThat(oAuthLoginResponseDto.getMemberId()).isEqualTo(InactiveMember.getId());
        assertThat(oAuthLoginResponseDto).hasNoNullFieldsOrProperties();

        verify(appleOAuthUserProvider).getAppleEmail(anyString());
        verify(jwtProvider).generateToken(anyString());
        verify(redisService).saveValue(anyString(), any(Token.class));
    }

    @DisplayName("회원가입 시 FullName 이 null 인 경우," +
                "INVALID_USER_NAME 예외가 발생해야 한다.")
    @Test
    void login_Fail_INVALID_USER_NAME() {
        // given
        appleAuthRequestDto = AppleAuthRequestDto.builder()
                .identityToken(IDENTITY_TOKEN)
                .build();

        given(appleOAuthUserProvider.getAppleEmail(anyString())).willReturn(EMAIL);

        // when & then
        assertThatThrownBy(() -> appleOAuthService.login(appleAuthRequestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_USER_NAME);

        verify(appleOAuthUserProvider).getAppleEmail(anyString());
    }

    @DisplayName("다른 SNS 계정으로 가입된 이메일인 경우," +
                "ALREADY_EXIST_SOCIAL_ACCOUNT 예외를 발생 시킨다.")
    @Test
    void login_Fail_ALREADY_EXIST_SOCIAL_ACCOUNT() {
        // given
        memberRepository.save(
                Member.builder()
                        .email(EMAIL)
                        .socialType(SocialType.KAKAO)
                        .build()
        );

        given(appleOAuthUserProvider.getAppleEmail(anyString())).willReturn(EMAIL);

        // when & then
        assertThatThrownBy(() -> appleOAuthService.login(appleAuthRequestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(ALREADY_EXIST_SOCIAL_ACCOUNT);

        verify(appleOAuthUserProvider).getAppleEmail(anyString());
    }

    @DisplayName("생성된 clientSecret 토큰을 AppleClientSecretResponseDto 로 반환해야 한다.")
    @Test
    void getClientSecret() {
        // given
        given(appleJwtProvider.createClientSecret()).willReturn("clientSecret");

        // when
        AppleClientSecretResponseDto appleClientSecretResponseDto = appleOAuthService.getClientSecret();

        // then
        assertThat(appleClientSecretResponseDto).hasNoNullFieldsOrProperties();

        verify(appleJwtProvider).createClientSecret();
    }
}