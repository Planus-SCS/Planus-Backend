package scs.planus.global.auth.service.apple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import scs.planus.global.util.encryptor.Encryptor;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AppleClaimsValidatorTest {
    private static final String ISS = "test_iss";
    private static final String CLIENT_ID = "test_clientId";
    private static final String NONCE = "test_nonce";
    private static final String NONCE_KEY = "nonce";

    private final AppleClaimsValidator appleClaimsValidator = new AppleClaimsValidator(ISS, CLIENT_ID, NONCE);

    @DisplayName("iss, clientId, nonce 모두 일치하면 True 를 반환 해야 한다.")
    @Test
    void isValid() {
        // given
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(NONCE_KEY, Encryptor.encryptWithSHA256(NONCE));

        Claims claims = Jwts.claims(claimsMap)
                .setIssuer(ISS)
                .setAudience(CLIENT_ID);

        // when & then
        assertThat(appleClaimsValidator.isValid(claims)).isTrue();
    }

    @DisplayName("iss, clientId, nonce 중 하나라도 일치하지 않으면 False 를 반환 해야 한다.")
    @ParameterizedTest
    @CsvSource({
            "invalid_nonce, test_iss, test_clientId",
            "test_nonce, invalid_iss, test_clientId",
            "test_nonce, test_iss, invalid_aud",
    })
    void isValid_Fail(String nonce, String iss, String clientId) {
        // given
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(NONCE_KEY, Encryptor.encryptWithSHA256(nonce));

        Claims claims = Jwts.claims(claimsMap)
                .setIssuer(iss)
                .setAudience(clientId);

        // when & then
        assertThat(appleClaimsValidator.isValid(claims)).isFalse();
    }
}