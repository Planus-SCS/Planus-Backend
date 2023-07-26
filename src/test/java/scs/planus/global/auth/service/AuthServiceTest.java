package scs.planus.global.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import scs.planus.global.auth.dto.TokenReissueRequestDto;
import scs.planus.global.auth.dto.TokenReissueResponseDto;
import scs.planus.global.auth.entity.Token;
import scs.planus.global.exception.PlanusException;
import scs.planus.infra.redis.RedisService;
import scs.planus.support.ServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static scs.planus.global.exception.CustomExceptionStatus.EXPIRED_REFRESH_TOKEN;
import static scs.planus.global.exception.CustomExceptionStatus.INVALID_REFRESH_TOKEN;

@ServiceTest
class AuthServiceTest {

    private static final String TEST_EMAIL = "test@test";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    @MockBean
    private RedisService redisService;
    @MockBean
    private JwtProvider jwtProvider;

    private AuthService authService;

    private TokenReissueRequestDto requestDto;
    private Token reissuedToken;

    @BeforeEach
    void init() {
        this.authService = new AuthService(redisService, jwtProvider);

        requestDto = TokenReissueRequestDto.builder()
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();

        reissuedToken = Token.builder()
                .accessToken("newAccessToken")
                .refreshToken("newRefreshToken")
                .refreshTokenExpiredIn(0L)
                .build();
    }

    @DisplayName("토큰이 재발급되어야 한다.")
    @Test
    void reissue(){
        //given
        given(jwtProvider.getPayload(ACCESS_TOKEN)).willReturn(TEST_EMAIL);
        given(jwtProvider.generateToken(TEST_EMAIL)).willReturn(reissuedToken);
        given(redisService.getValue(TEST_EMAIL)).willReturn(REFRESH_TOKEN);

        //when
        TokenReissueResponseDto reissue = authService.reissue(requestDto);

        //then
        assertThat(reissue.getAccessToken()).isEqualTo(reissuedToken.getAccessToken());
        assertThat(reissue.getRefreshToken()).isEqualTo(reissuedToken.getRefreshToken());
    }

    @DisplayName("토큰이 만료되어 Redis에 없는 경우, 예외를 던진다.")
    @Test
    void reissue_Throw_Exception_If_Expired_Token(){
        //given
        given(jwtProvider.getPayload(ACCESS_TOKEN)).willReturn(TEST_EMAIL);
        given(jwtProvider.generateToken(TEST_EMAIL)).willReturn(reissuedToken);
        given(redisService.getValue(TEST_EMAIL)).willReturn(null);

        //then
        assertThatThrownBy(() ->
                authService.reissue(requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(EXPIRED_REFRESH_TOKEN);
    }

    @DisplayName("토큰이 만료되어 Redis에 없는 경우, 예외를 던진다.")
    @Test
    void reissue_Throw_Exception_If_Invalid_Token(){
        //given
        String invalidRefreshToken = "invalidRefreshToken";
        given(jwtProvider.getPayload(ACCESS_TOKEN)).willReturn(TEST_EMAIL);
        given(jwtProvider.generateToken(TEST_EMAIL)).willReturn(reissuedToken);
        given(redisService.getValue(TEST_EMAIL)).willReturn(invalidRefreshToken);

        //then
        assertThatThrownBy(() ->
                authService.reissue(requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_REFRESH_TOKEN);
    }
}