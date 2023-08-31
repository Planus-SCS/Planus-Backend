package scs.planus.global.auth.service.apple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.global.exception.PlanusException;

import java.security.*;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scs.planus.global.exception.CustomExceptionStatus.*;

class AppleJwtParserTest {
    private static final String ALG_KEY = "alg";
    private static final String KID_KEY = "kid";
    private static final String KID = "kid";
    private static final String EMAIL_KEY = "email";
    private static final String EMAIL = "email";
    private static final String INVALID_TOKEN = "invalidToken";

    private final AppleJwtParser appleJwtParser;

    private JwtBuilder defaultJwtBuilder;
    private String identityToken;
    private KeyPair keyPair;

    public AppleJwtParserTest() {
        this.appleJwtParser = new AppleJwtParser();
    }

    @BeforeEach
    void init() {
        Date now = new Date();
        long expirationTime = 24 * 60 * 60 * 1000;

        keyPair = generateKeyPair();

        defaultJwtBuilder = Jwts.builder()
                .setHeaderParam(KID_KEY, KID)
                .claim(EMAIL_KEY, EMAIL)
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256);

        identityToken = defaultJwtBuilder
                .compact();
    }

    @DisplayName("Apple 의 identityToken 으로 부터 headers 를 파싱할 수 있다.")
    @Test
    void parseHeaders_Success() {
        // when
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);

        // then
        assertThat(headers).containsKeys(ALG_KEY, KID_KEY);
        assertThat(headers.get(ALG_KEY)).isEqualTo("RS256");
        assertThat(headers.get(KID_KEY)).isEqualTo(KID);
    }

    @DisplayName("올바르지 않은 형식의 identityToken 으로 헤더를 파싱할 경우," +
                "INVALID_APPLE_IDENTITY_TOKEN 예외를 발생 시킨다.")
    @Test
    void parseHeaders_Fail() {
        // when & then
        assertThatThrownBy(() -> appleJwtParser.parseHeaders(INVALID_TOKEN))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_APPLE_IDENTITY_TOKEN);
    }

    @DisplayName("Apple 의 PublicKey 로부터 identityToken 의 claim 을 파싱할 수 있다.")
    @Test
    void parseClaimWithPublicKey() {
        // when
        Claims claims = appleJwtParser.parseClaimWithPublicKey(identityToken, keyPair.getPublic());

        // then
        assertThat(claims.get(EMAIL_KEY, String.class)).isEqualTo(EMAIL);
    }

    @DisplayName("만료된 Apple identityToken 을 PublicKey 로 파싱할 경우," +
                "UNAUTHORIZED_ACCESS_TOKEN 예외를 발생 시킨다.")
    @Test
    void parseClaimWithPublicKey_Fail_Expired() {
        // given
        Date now = new Date();
        long expirationTime = -1L;

        String expiredToken = defaultJwtBuilder
                .setExpiration(new Date(now.getTime() + expirationTime))
                .compact();

        // when & then
        assertThatThrownBy(() -> appleJwtParser.parseClaimWithPublicKey(expiredToken, keyPair.getPublic()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(UNAUTHORIZED_ACCESS_TOKEN);
    }

    @DisplayName("올바르지 않은 형식의 identityToken 으로 헤더를 파싱할 경우," +
                "INVALID_APPLE_IDENTITY_TOKEN 예외를 발생 시킨다.")
    @Test
    void parseClaimWithPublicKey_Fail_Invalid_IdentityToken() {
        // when & then
        assertThatThrownBy(() -> appleJwtParser.parseClaimWithPublicKey(INVALID_TOKEN, keyPair.getPublic()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_APPLE_IDENTITY_TOKEN);
    }

    @DisplayName("잘못된 PublicKey 로 Apple identityToken 를 파싱할 경우," +
                "INTERNAL_SERVER_ERROR 예외를 발생 시킨다.")
    @Test
    void parseClaimWithPublicKey_Fail_Invalid_PublicKey() {
        // given
        PublicKey invalidPublicKey = generateKeyPair().getPublic();

        // when & then
        assertThatThrownBy(() -> appleJwtParser.parseClaimWithPublicKey(identityToken, invalidPublicKey))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INTERNAL_SERVER_ERROR);
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