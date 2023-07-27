package scs.planus.global.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.global.auth.entity.Token;
import scs.planus.global.exception.PlanusException;
import scs.planus.support.ServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scs.planus.global.exception.CustomExceptionStatus.UNAUTHORIZED_ACCESS_TOKEN;

class JwtProviderTest extends ServiceTest {

    private static final String SECRET_KEY = "A".repeat(64);
    private static final int ACCESS_TOKEN_EXPIRED_IN = 3600;
    private static final int REFRESH_TOKEN_EXPIRED_IN = 7200;
    private static final String PAYLOAD = "test@test";

    private final JwtProvider jwtProvider;

    @Autowired
    public JwtProviderTest() {
        this.jwtProvider = new JwtProvider(SECRET_KEY, ACCESS_TOKEN_EXPIRED_IN, REFRESH_TOKEN_EXPIRED_IN);
    }

    @DisplayName("토큰이 제대로 생성되어야 한다.")
    @Test
    void generateToken(){
        //when
        Token token = jwtProvider.generateToken(PAYLOAD);

        //then
        assertThat(token.getAccessToken().split("\\.")).hasSize(3);
        assertThat(token.getRefreshToken().split("\\.")).hasSize(3);
        assertThat(token.getRefreshTokenExpiredIn()).isEqualTo(REFRESH_TOKEN_EXPIRED_IN);
    }

    @DisplayName("토큰이 유효하다면, 검증 시 true를 반환한다.")
    @Test
    void isValidToken(){
        //given
        Token token = jwtProvider.generateToken(PAYLOAD);

        //when
        boolean isValid = jwtProvider.isValidToken(token.getAccessToken());

        //then
        assertThat(isValid).isTrue();
    }

    @DisplayName("토큰이 만료되었다면, 검증 시 false를 반환한다.")
    @Test
    void isValidToken_Return_False_If_Expired(){
        //given
        JwtProvider expiredJwtProvider = new JwtProvider(SECRET_KEY, 0, 0);
        Token token = expiredJwtProvider.generateToken(PAYLOAD);

        //when
        boolean isValid = expiredJwtProvider.isValidToken(token.getAccessToken());

        //then
        assertThat(isValid).isFalse();
    }

    @DisplayName("AccessToken의 payload를 파싱한다.")
    @Test
    void getPayload(){
        //given
        Token token = jwtProvider.generateToken(PAYLOAD);

        //when
        String payload = jwtProvider.getPayload(token.getAccessToken());

        //then
        assertThat(payload).isEqualTo(PAYLOAD);
    }

    @DisplayName("AccessToken이 만료되었더라도 이를 파싱한다.")
    @Test
    void getPayload_If_Expired_Token(){
        //given
        JwtProvider expiredJwtProvider = new JwtProvider(SECRET_KEY, 0L, 0L);
        Token token = expiredJwtProvider.generateToken(PAYLOAD);

        //when
        String payload = expiredJwtProvider.getPayload(token.getAccessToken());

        //then
        assertThat(payload).isEqualTo(PAYLOAD);
    }

    @DisplayName("AccessToken 형식이 잘못된 경우, 예외를 던진다")
    @Test
    void getPayload_Throw_Exception_If_Invalid_Token(){
        //given
        String invalidToken = "invalidToken";

        //when
        assertThatThrownBy(() ->
                jwtProvider.getPayload(invalidToken))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(UNAUTHORIZED_ACCESS_TOKEN);
    }

    @DisplayName("RefreshToken에는 payload가 존재하지 않는다.")
    @Test
    void getPayload_Return_Null_If_Refresh_Token(){
        //given
        Token token = jwtProvider.generateToken(PAYLOAD);

        //when
        String payload = jwtProvider.getPayload(token.getRefreshToken());

        //then
        assertThat(payload).isNull();
    }
}