package scs.planus.global.auth.service.apple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.global.exception.PlanusException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scs.planus.global.exception.CustomExceptionStatus.INTERNAL_SERVER_ERROR;

class AppleJwtProviderTest {
    private static final String KEY_FACTORY_INSTANCE_ALGO = "EC";
    private AppleJwtProvider appleJwtProvider;

    @DisplayName("ClientSecretKey 로 부터 ClientSecret 토큰이 정상적으로 생성 되어야 한다.")
    @Test
    void createClientSecret() throws NoSuchAlgorithmException {
        // given
        appleJwtProvider = new AppleJwtProvider(
                "clientId",
                createClientSecretKey(),
                10000,
                "keyId",
                "teamId",
                "authUrl"
        );

        // when
        String clientSecret = appleJwtProvider.createClientSecret();

        // then
        assertThat(clientSecret).isNotNull();
    }

    @DisplayName("유효하지 않은 ClientSecretKey 일 경우," +
                "INTERNAL_SERVER_ERROR 예외를 발생시켜야 한다.")
    @Test
    void createClientSecret_f() {
        // given
        appleJwtProvider = new AppleJwtProvider(
                "clientId",
                "invalid",
                10000,
                "keyId",
                "teamId",
                "authUrl"
        );

        // when & then
        assertThatThrownBy(() -> appleJwtProvider.createClientSecret())
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INTERNAL_SERVER_ERROR);
    }

    private String createClientSecretKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_FACTORY_INSTANCE_ALGO);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] privateKeyEncoded = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyEncoded);
    }
}