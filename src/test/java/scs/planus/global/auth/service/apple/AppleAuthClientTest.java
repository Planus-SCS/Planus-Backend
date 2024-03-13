package scs.planus.global.auth.service.apple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.global.auth.entity.ApplePublicKey;
import scs.planus.global.auth.entity.ApplePublicKeys;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class AppleAuthClientTest {

    private AppleAuthClient appleAuthClient;

    public AppleAuthClientTest() {
        this.appleAuthClient = new AppleAuthClient("https://appleid.apple.com/auth/keys");
    }

    @DisplayName("Apple Server 로 요청을 보내 PublicKey 를 응답 받아야 한다.")
    @Test
    void getApplePublicKey() {
        // when
        ApplePublicKeys applePublicKeys = appleAuthClient.getApplePublicKey();

        boolean isRequestedKeysNonNull = applePublicKeys.getKeys().stream()
                .allMatch(this::isAllNotNull);

        // then
        assertThat(applePublicKeys.getKeys()).hasSize(3);
        assertThat(isRequestedKeysNonNull).isTrue();
    }

    private boolean isAllNotNull(ApplePublicKey applePublicKey) {
        return Objects.nonNull(applePublicKey.getKty()) && Objects.nonNull(applePublicKey.getKid()) &&
                Objects.nonNull(applePublicKey.getUse()) && Objects.nonNull(applePublicKey.getAlg()) &&
                Objects.nonNull(applePublicKey.getN()) && Objects.nonNull(applePublicKey.getE());
    }
}