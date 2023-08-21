package scs.planus.global.auth.service.apple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import scs.planus.global.auth.entity.apple.ApplePublicKey;
import scs.planus.global.auth.entity.apple.ApplePublicKeys;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.util.encryptor.Encryptor;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static scs.planus.global.exception.CustomExceptionStatus.INVALID_APPLE_IDENTITY_TOKEN;
import static scs.planus.global.exception.CustomExceptionStatus.NO_SUCH_ALGORITHM;

class AppleOAuthUserProviderTest {
    private static final String EMAIL = "email";

    @Mock
    private AppleJwtParser appleJwtParser;
    @Mock
    private AppleAuthClient appleAuthClient;
    @Mock
    private ApplePublicKeyGenerator applePublicKeyGenerator;
    @Mock
    private AppleClaimsValidator appleClaimsValidator;

    private final AppleOAuthUserProvider appleOAuthUserProvider;

    private String identityToken;

    public AppleOAuthUserProviderTest() {
        MockitoAnnotations.openMocks(this);
        appleOAuthUserProvider = new AppleOAuthUserProvider(
                appleJwtParser,
                appleAuthClient,
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
                .claim("email", EMAIL)
                .claim("nonce", Encryptor.encryptWithSHA256("nonce"))
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("clientId")
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();

        String N = RandomStringUtils.randomAlphanumeric(128);
        String E = RandomStringUtils.randomAlphanumeric(4);
        ApplePublicKeys applePublicKeys = new ApplePublicKeys(
                List.of(new ApplePublicKey("RSA",  "kid", "use", "RS256", N, E))
        );

        Map<String, String> heads = new HashMap<>();
        heads.put("alg", "RS256");
        heads.put("kid", "kid");

        Claims claims = Jwts.claims()
                .setIssuer("iss")
                .setAudience("clientId");

        claims.put("nonce", Encryptor.encryptWithSHA256("nonce"));
        claims.put("email", "email");

        given(appleJwtParser.parseHeaders(anyString())).willReturn(heads);
        given(appleAuthClient.getApplePublicKey()).willReturn(applePublicKeys);
        given(applePublicKeyGenerator.generatePublicKey(anyMap(), any(ApplePublicKeys.class))).willReturn(keyPair.getPublic());
        given(appleJwtParser.parseClaimWithPublicKey(anyString(), any(PublicKey.class))).willReturn(claims);
    }

    @DisplayName("identityToken 으로 부터 사용자 email 을 얻을 수 있다.")
    @Test
    void getAppleEmail() {
        // given
        given(appleClaimsValidator.isValid(any(Claims.class))).willReturn(true);

        // when
        String appleEmail = appleOAuthUserProvider.getAppleEmail(identityToken);

        // then
        assertThat(appleEmail).isEqualTo(EMAIL);

        verify(appleJwtParser).parseHeaders(anyString());
        verify(appleAuthClient).getApplePublicKey();
        verify(applePublicKeyGenerator).generatePublicKey(anyMap(), any(ApplePublicKeys.class));
        verify(appleJwtParser).parseClaimWithPublicKey(anyString(), any(PublicKey.class));
        verify(appleClaimsValidator).isValid(any(Claims.class));
    }

    @DisplayName("identity claims 의 검증 결과가 false 인 경우," +
                "INVALID_APPLE_IDENTITY_TOKEN 예외가 발생해야 한다.")
    @Test
    void getAppleEmail_Fail_INVALID_APPLE_IDENTITY_TOKEN() {
        // given
        given(appleClaimsValidator.isValid(any(Claims.class))).willReturn(false);

        // when & then
        Assertions.assertThatThrownBy(() -> appleOAuthUserProvider.getAppleEmail(identityToken))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_APPLE_IDENTITY_TOKEN);

        verify(appleJwtParser).parseHeaders(anyString());
        verify(appleAuthClient).getApplePublicKey();
        verify(applePublicKeyGenerator).generatePublicKey(anyMap(), any(ApplePublicKeys.class));
        verify(appleJwtParser).parseClaimWithPublicKey(anyString(), any(PublicKey.class));
        verify(appleClaimsValidator).isValid(any(Claims.class));
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