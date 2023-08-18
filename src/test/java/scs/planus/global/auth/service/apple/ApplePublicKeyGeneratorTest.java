package scs.planus.global.auth.service.apple;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.global.auth.entity.apple.ApplePublicKey;
import scs.planus.global.auth.entity.apple.ApplePublicKeys;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ApplePublicKeyGeneratorTest {
    private static final String ALG = "alg";
    private static final String ALG_KEY = "alg";
    private static final String KID = "kid";
    private static final String KID_KEY = "kid";
    private static final String KTY = "RSA";
    private static final String USE = "use";
    private static final Integer LEN_N = 128;
    private static final Integer LEN_E = 4;

    private final ApplePublicKeyGenerator applePublicKeyGenerator = new ApplePublicKeyGenerator();

    @DisplayName("header 와 ApplePublicKeys 로 PublicKey 를 생성할 수 있다.")
    @Test
    void generatePublicKey() {
        // given
        Map<String, String> heads = new HashMap<>();
        heads.put(ALG_KEY, ALG);
        heads.put(KID_KEY, KID);

        String N = RandomStringUtils.randomAlphanumeric(LEN_N);
        String E = RandomStringUtils.randomAlphanumeric(LEN_E);

        ApplePublicKeys applePublicKeys = new ApplePublicKeys(
                List.of(new ApplePublicKey(KTY, KID, USE, ALG, N, E)));

        // when
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(heads, applePublicKeys);

        // then
        assertThat(publicKey).isNotNull();
    }
}