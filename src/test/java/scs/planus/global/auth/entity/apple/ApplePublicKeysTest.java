package scs.planus.global.auth.entity.apple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.global.auth.entity.ApplePublicKey;
import scs.planus.global.auth.entity.ApplePublicKeys;
import scs.planus.global.exception.PlanusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scs.planus.global.exception.CustomExceptionStatus.INVALID_ALG_KID_INFO;

class ApplePublicKeysTest {
    private static final String ALG = "alg";
    private static final String KID = "kid";
    private static final String KTY = "RSA";
    private static final String USE = "use";
    private static final String N = "n";
    private static final String E = "e";
    private static final String UN_MATCH = "un match value";

    @DisplayName("alg 과 kid 가 일치하는 applePublicKey 를 반환해야 한다.")
    @Test
    void getKeys() {
        // given
        ApplePublicKey applePublicKey = new ApplePublicKey(KTY, KID, USE, ALG, N, E);

        ApplePublicKeys applePublicKeys = new ApplePublicKeys(
                List.of(applePublicKey,
                        new ApplePublicKey(KTY,  UN_MATCH, USE, ALG, N, E),
                        new ApplePublicKey(KTY, KID, USE, UN_MATCH, N, E))
        );

        // when
        ApplePublicKey matchesKey = applePublicKeys.getMatchesKey(ALG, KID);

        // then
        assertThat(matchesKey).isEqualTo(applePublicKey);
    }

    @DisplayName("alg 과 kid 가 일치하는 applePublicKey 가 없는 경우" +
                "INVALID_ALG_KID_INFO 예외가 발생해야 한다.")
    @Test
    void getKeys_Fail() {
        // given
        ApplePublicKey applePublicKey = new ApplePublicKey(KTY, KID, USE, ALG, N, E);

        ApplePublicKeys applePublicKeys = new ApplePublicKeys(
                List.of(applePublicKey,
                        new ApplePublicKey(KTY,  UN_MATCH, USE, ALG, N, E),
                        new ApplePublicKey(KTY, KID, USE, UN_MATCH, N, E))
        );

        // when & then
        assertThatThrownBy(() -> applePublicKeys.getMatchesKey(UN_MATCH, UN_MATCH))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_ALG_KID_INFO);
    }
}