package scs.planus.global.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import scs.planus.global.auth.entity.userinfo.AppleUserInfo;
import scs.planus.global.auth.entity.userinfo.GoogleUserInfo;
import scs.planus.global.auth.entity.userinfo.KakaoUserInfo;
import scs.planus.global.auth.entity.userinfo.OAuthUserInfo;
import scs.planus.global.auth.service.apple.AppleJwtProvider;
import scs.planus.global.auth.service.apple.AppleOAuthUserProvider;
import scs.planus.global.auth.service.google.GoogleOAuthUserProvider;
import scs.planus.global.auth.service.kakao.KakaoOAuthUserProvider;
import scs.planus.global.exception.PlanusException;
import scs.planus.infra.redis.RedisService;
import scs.planus.support.ServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static scs.planus.global.exception.CustomExceptionStatus.ALREADY_EXIST_SOCIAL_ACCOUNT;

@Slf4j
class OAuthServiceTest extends ServiceTest {

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private final MemberRepository memberRepository;
    @MockBean
    private final JwtProvider jwtProvider;
    @MockBean
    private final RedisService redisService;
    @MockBean
    private final KakaoOAuthUserProvider kakaoOAuthUserProvider;
    @MockBean
    private final GoogleOAuthUserProvider googleOAuthUserProvider;
    @MockBean
    private final AppleOAuthUserProvider appleOAuthUserProvider;
    @MockBean
    private final AppleJwtProvider appleJwtProvider;

    private final OAuthService oAuthService;

    private AppleAuthRequestDto appleAuthRequestDto;

    private Token token;

    @Autowired
    public OAuthServiceTest(MemberRepository memberRepository,
                            JwtProvider jwtProvider,
                            RedisService redisService,
                            KakaoOAuthUserProvider kakaoOAuthUserProvider,
                            GoogleOAuthUserProvider googleOAuthUserProvider,
                            AppleOAuthUserProvider appleOAuthUserProvider,
                            AppleJwtProvider appleJwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
        this.kakaoOAuthUserProvider = kakaoOAuthUserProvider;
        this.googleOAuthUserProvider = googleOAuthUserProvider;
        this.appleOAuthUserProvider = appleOAuthUserProvider;
        this.appleJwtProvider = appleJwtProvider;

        oAuthService = new OAuthService(
                memberRepository,
                jwtProvider,
                redisService,
                kakaoOAuthUserProvider,
                googleOAuthUserProvider,
                appleOAuthUserProvider,
                appleJwtProvider
        );
    }

    @BeforeEach
    void init() {
        token = Token.builder()
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();
    }

    @DisplayName("Kakao OAuth test")
    @Nested
    class KakaoOAuthTest {
        private static final String CODE = "CODE";
        private static final String EMAIL = "kakao@test.com";
        private static final String NICKNAME = "KAKAO_USER";
        private OAuthUserInfo kakaoUserInfo;

        @BeforeEach
        void init() {
            KakaoUserInfo.KakaoAccount kakaoAccount = KakaoUserInfo.KakaoAccount.builder()
                    .email(EMAIL)
                    .profile(new KakaoUserInfo.KakaoAccount.Profile(NICKNAME))
                    .build();

            kakaoUserInfo = KakaoUserInfo.builder()
                    .kakaoAccount(kakaoAccount)
                    .build();
        }

        @DisplayName("카카오 소셜 로그인이 정상적으로 이루어져야 한다.")
        @Test
        void kakaoLogin() {
            //given
            given(kakaoOAuthUserProvider.getUserInfo(anyString()))
                    .willReturn(kakaoUserInfo);
            given(jwtProvider.generateToken(anyString())).willReturn(token);
            willDoNothing().given(redisService).saveValue(anyString(), any(Token.class));

            //when
            OAuthLoginResponseDto oAuthLoginResponseDto = oAuthService.kakaoLogin(CODE);

            //then
            assertAll(
                    () -> assertThat(oAuthLoginResponseDto.getMemberId()).isNotNull(),
                    () -> assertThat(oAuthLoginResponseDto.getAccessToken()).isEqualTo(ACCESS_TOKEN),
                    () -> assertThat(oAuthLoginResponseDto.getRefreshToken()).isEqualTo(REFRESH_TOKEN)
            );
            verify(kakaoOAuthUserProvider).getUserInfo(anyString());
            verify(jwtProvider).generateToken(anyString());
            verify(redisService).saveValue(anyString(), any(Token.class));
        }

        @DisplayName("카카오, 비활성화된 계정일 경우, 재활성화 및 사용자 정보 초기화가 되어야 한다.")
        @Test
        void kakaoLogin_saveOrGetExistedMember() {
            // given
            Member inActiveMember = memberRepository.save(
                    Member.builder()
                            .nickname("비활성계정")
                            .email(EMAIL)
                            .socialType(SocialType.KAKAO)
                            .status(Status.INACTIVE)
                            .build()
            );

            given(kakaoOAuthUserProvider.getUserInfo(anyString())).willReturn(kakaoUserInfo);
            given(jwtProvider.generateToken(anyString())).willReturn(token);
            willDoNothing().given(redisService).saveValue(anyString(), any(Token.class));

            // when
            OAuthLoginResponseDto oAuthLoginResponseDto = oAuthService.kakaoLogin(CODE);

            // then
            assertAll(
                    () -> assertThat(oAuthLoginResponseDto.getMemberId()).isEqualTo(inActiveMember.getId()),
                    () -> assertThat(inActiveMember.getNickname()).isEqualTo(NICKNAME),
                    () -> assertThat(inActiveMember.getStatus()).isEqualTo(Status.ACTIVE)
            );
            verify(kakaoOAuthUserProvider).getUserInfo(anyString());
            verify(jwtProvider).generateToken(anyString());
            verify(redisService).saveValue(anyString(), any(Token.class));
        }

        @DisplayName("카카오, 이미 가입된 이메일인 경우, 예외가 발생한다.")
        @Test
        void kakaoLogin_Throw_Exception_If_Duplicated_Email() {
            // given
            Member member = memberRepository.save(
                    Member.builder()
                            .nickname("이미 존재하는계정")
                            .email(EMAIL)
                            .socialType(SocialType.GOOGLE)
                            .status(Status.ACTIVE)
                            .build()
            );

            given(kakaoOAuthUserProvider.getUserInfo(anyString())).willReturn(kakaoUserInfo);

            // when & then
            assertThatThrownBy(() ->
                    oAuthService.kakaoLogin(CODE))
                    .isInstanceOf(PlanusException.class)
                    .extracting("status")
                    .isEqualTo(ALREADY_EXIST_SOCIAL_ACCOUNT);
        }
    }

    @DisplayName("Google OAuth test")
    @Nested
    class googleOAuthTest {
        private static final String CODE = "CODE";
        private static final String EMAIL = "google@test.com";
        private static final String NICKNAME = "GOOGLE_USER";
        private OAuthUserInfo googleUserInfo;

        @BeforeEach
        void init() {
            googleUserInfo = GoogleUserInfo.builder()
                    .email(EMAIL)
                    .nickname(NICKNAME)
                    .build();
        }

        @DisplayName("구글 소셜 로그인이 정상적으로 이루어져야 한다.")
        @Test
        void googleLogin() {
            //given
            given(googleOAuthUserProvider.getUserInfo(anyString()))
                    .willReturn(googleUserInfo);
            given(jwtProvider.generateToken(anyString())).willReturn(token);
            willDoNothing().given(redisService).saveValue(anyString(), any(Token.class));

            //when
            OAuthLoginResponseDto oAuthLoginResponseDto = oAuthService.googleLogin(CODE);

            //then
            assertAll(
                    () -> assertThat(oAuthLoginResponseDto.getMemberId()).isNotNull(),
                    () -> assertThat(oAuthLoginResponseDto.getAccessToken()).isEqualTo(ACCESS_TOKEN),
                    () -> assertThat(oAuthLoginResponseDto.getRefreshToken()).isEqualTo(REFRESH_TOKEN)
            );
            verify(googleOAuthUserProvider).getUserInfo(anyString());
            verify(jwtProvider).generateToken(anyString());
            verify(redisService).saveValue(anyString(), any(Token.class));
        }

        @DisplayName("구글, 비활성화된 계정일 경우, 재활성화 및 사용자 정보 초기화가 되어야 한다.")
        @Test
        void googleLogin_saveOrGetExistedMember() {
            // given
            Member inActiveMember = memberRepository.save(
                    Member.builder()
                            .nickname("비활성계정")
                            .email(EMAIL)
                            .socialType(SocialType.GOOGLE)
                            .status(Status.INACTIVE)
                            .build()
            );

            given(googleOAuthUserProvider.getUserInfo(anyString())).willReturn(googleUserInfo);
            given(jwtProvider.generateToken(anyString())).willReturn(token);
            willDoNothing().given(redisService).saveValue(anyString(), any(Token.class));

            // when
            OAuthLoginResponseDto oAuthLoginResponseDto = oAuthService.googleLogin(CODE);

            // then
            assertAll(
                    () -> assertThat(oAuthLoginResponseDto.getMemberId()).isEqualTo(inActiveMember.getId()),
                    () -> assertThat(inActiveMember.getNickname()).isEqualTo(NICKNAME),
                    () -> assertThat(inActiveMember.getStatus()).isEqualTo(Status.ACTIVE)
            );
            verify(googleOAuthUserProvider).getUserInfo(anyString());
            verify(jwtProvider).generateToken(anyString());
            verify(redisService).saveValue(anyString(), any(Token.class));
        }

        @DisplayName("구글, 이미 가입된 이메일인 경우, 예외가 발생한다.")
        @Test
        void googleLogin_Throw_Exception_If_Duplicated_Email() {
            // given
            Member member = memberRepository.save(
                    Member.builder()
                            .nickname("이미 존재하는계정")
                            .email(EMAIL)
                            .socialType(SocialType.KAKAO)
                            .status(Status.ACTIVE)
                            .build()
            );

            given(googleOAuthUserProvider.getUserInfo(anyString())).willReturn(googleUserInfo);

            // when & then
            assertThatThrownBy(() ->
                    oAuthService.googleLogin(CODE))
                    .isInstanceOf(PlanusException.class)
                    .extracting("status")
                    .isEqualTo(ALREADY_EXIST_SOCIAL_ACCOUNT);
        }
    }

    @DisplayName("Apple OAuth Test")
    @Nested
    class AppleOAuthTest {

        private static final String IDENTITY_TOKEN = "identityToken";
        private static final String EMAIL = "email";
        private OAuthUserInfo appleUserInfo;

        @BeforeEach
        void init() {
            appleUserInfo = new AppleUserInfo(EMAIL);

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
            given(appleOAuthUserProvider.getUserInfo(anyString())).willReturn(appleUserInfo);
            given(jwtProvider.generateToken(anyString())).willReturn(token);
            willDoNothing().given(redisService).saveValue(anyString(), any(Token.class));

            // when
            OAuthLoginResponseDto oAuthLoginResponseDto = oAuthService.appleLogin(appleAuthRequestDto);

            // then
            assertThat(oAuthLoginResponseDto).hasNoNullFieldsOrProperties();

            verify(appleOAuthUserProvider).getUserInfo(anyString());
            verify(jwtProvider).generateToken(anyString());
            verify(redisService).saveValue(anyString(), any(Token.class));
        }

        @DisplayName("비활성화 계정일 경우, 재활성화 및 사용자 정보 초기화가 되어야 한다.")
        @Test
        void login_Success_Reactivation() {
            // given
            Member inActiveMember = memberRepository.save(
                    Member.builder()
                            .nickname("비활성계정")
                            .email(EMAIL)
                            .socialType(SocialType.APPLE)
                            .status(Status.INACTIVE)
                            .build()
            );

            given(appleOAuthUserProvider.getUserInfo(anyString())).willReturn(appleUserInfo);
            given(jwtProvider.generateToken(anyString())).willReturn(token);
            willDoNothing().given(redisService).saveValue(anyString(), any(Token.class));

            // when
            OAuthLoginResponseDto oAuthLoginResponseDto = oAuthService.appleLogin(appleAuthRequestDto);

            // then
            assertThat(oAuthLoginResponseDto.getMemberId()).isEqualTo(inActiveMember.getId());
            assertThat(oAuthLoginResponseDto).hasNoNullFieldsOrProperties();

            verify(appleOAuthUserProvider).getUserInfo(anyString());
            verify(jwtProvider).generateToken(anyString());
            verify(redisService).saveValue(anyString(), any(Token.class));
        }

        @DisplayName("회원가입 시 FullName 이 null 인 경우, user# 로 시작하는 임의의 이름이 설정된다.")
        @Test
        void login_Fail_INVALID_USER_NAME() {
            // given
            appleAuthRequestDto = AppleAuthRequestDto.builder()
                    .identityToken(IDENTITY_TOKEN)
                    .build();

            given(appleOAuthUserProvider.getUserInfo(anyString())).willReturn(appleUserInfo);
            given(jwtProvider.generateToken(anyString())).willReturn(token);
            willDoNothing().given(redisService).saveValue(anyString(), any(Token.class));

            // when
            OAuthLoginResponseDto oAuthLoginResponseDto = oAuthService.appleLogin(appleAuthRequestDto);
            Member member = memberRepository.findById(oAuthLoginResponseDto.getMemberId()).orElse(null);

            // then
            assertThat(oAuthLoginResponseDto).hasNoNullFieldsOrProperties();
            assertThat(member).isNotNull();
            assertThat(member.getNickname()).startsWith("user#");

            verify(appleOAuthUserProvider).getUserInfo(anyString());
            verify(jwtProvider).generateToken(anyString());
            verify(redisService).saveValue(anyString(), any(Token.class));
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

            given(appleOAuthUserProvider.getUserInfo(anyString())).willReturn(appleUserInfo);

            // when & then
            assertThatThrownBy(() -> oAuthService.appleLogin(appleAuthRequestDto))
                    .isInstanceOf(PlanusException.class)
                    .extracting("status")
                    .isEqualTo(ALREADY_EXIST_SOCIAL_ACCOUNT);

            verify(appleOAuthUserProvider).getUserInfo(anyString());
        }

        @DisplayName("생성된 clientSecret 토큰을 AppleClientSecretResponseDto 로 반환해야 한다.")
        @Test
        void getClientSecret() {
            // given
            given(appleJwtProvider.createClientSecret()).willReturn("clientSecret");

            // when
            AppleClientSecretResponseDto appleClientSecretResponseDto = oAuthService.getClientSecret();

            // then
            assertThat(appleClientSecretResponseDto).hasNoNullFieldsOrProperties();

            verify(appleJwtProvider).createClientSecret();
        }
    }
}