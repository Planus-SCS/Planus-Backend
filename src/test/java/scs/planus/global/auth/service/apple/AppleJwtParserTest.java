package scs.planus.global.auth.service.apple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
    private static final String INVALID_TOKEN = "invalidToken";
    private static final String KID = "fh6Bs8C";
    private static final String TEST_EMAIL = "planus@planus";

    private final AppleJwtParser appleJwtParser;

    public AppleJwtParserTest() {
        this.appleJwtParser = new AppleJwtParser();
    }

    @DisplayName("Apple 의 identityToken 으로 부터 headers 를 파싱할 수 있다.")
    @Test
    void parseHeaders_Success() throws NoSuchAlgorithmException {
        // given
        Date now = new Date();
        long expirationTime = 24 * 60 * 60 * 1000;

        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();

        String identityToken = Jwts.builder()
                .setHeaderParam("kid", KID)
                .claim("email", TEST_EMAIL)
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        // when
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);

        // then
        assertThat(headers).containsKeys("alg", "kid");
        assertThat(headers.get("alg")).isEqualTo("RS256");
        assertThat(headers.get("kid")).isEqualTo(KID);
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
    void parseClaimWithPublicKey() throws NoSuchAlgorithmException {
        // given
        Date now = new Date();
        long expirationTime = 24 * 60 * 60 * 1000;

        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        String identityToken = Jwts.builder()
                .setHeaderParam("kid", KID)
                .claim("email", TEST_EMAIL)
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        // when
        Claims claims = appleJwtParser.parseClaimWithPublicKey(identityToken, publicKey);

        // then
        assertThat(claims.get("email", String.class)).isEqualTo(TEST_EMAIL);
    }

    @DisplayName("만료된 Apple identityToken 을 PublicKey 로 파싱할 경우," +
                "UNAUTHORIZED_ACCESS_TOKEN 예외를 발생 시킨다.")
    @Test
    void parseClaimWithPublicKey_Fail_Expired() throws NoSuchAlgorithmException {
        // given
        Date now = new Date();
        long expirationTime = -1L;

        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        String identityToken = Jwts.builder()
                .setHeaderParam("kid", KID)
                .claim("email", TEST_EMAIL)
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        // when & then
        assertThatThrownBy(() -> appleJwtParser.parseClaimWithPublicKey(identityToken, publicKey))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(UNAUTHORIZED_ACCESS_TOKEN);
    }

    @DisplayName("올바르지 않은 형식의 identityToken 으로 헤더를 파싱할 경우," +
                "INVALID_APPLE_IDENTITY_TOKEN 예외를 발생 시킨다.")
    @Test
    void parseClaimWithPublicKey_Fail_Invalid_IdentityToken() throws NoSuchAlgorithmException {
        // given
        PublicKey publicKey = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair()
                .getPublic();

        // when & then
        assertThatThrownBy(() -> appleJwtParser.parseClaimWithPublicKey(INVALID_TOKEN, publicKey))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_APPLE_IDENTITY_TOKEN);
    }

    @DisplayName("잘못된 PublicKey 로 Apple identityToken 를 파싱할 경우," +
                "INTERNAL_SERVER_ERROR 예외를 발생 시킨다.")
    @Test
    void parseClaimWithPublicKey_Fail_Invalid_PublicKey() throws NoSuchAlgorithmException {
        // given
        Date now = new Date();
        long expirationTime = 24 * 60 * 60 * 1000;

        PrivateKey privateKey = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair()
                .getPrivate();

        PublicKey invalidPublicKey = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair()
                .getPublic();

        String identityToken = Jwts.builder()
                .setHeaderParam("kid", KID)
                .claim("email", TEST_EMAIL)
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        // when & then
        assertThatThrownBy(() -> appleJwtParser.parseClaimWithPublicKey(identityToken, invalidPublicKey))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INTERNAL_SERVER_ERROR);
    }
}