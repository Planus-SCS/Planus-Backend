package scs.planus.global.util.encryptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EncryptorTest {
    private static final String TEST_STRING = "test";
    private static final String ENCRYPT_WITH_SHA256_RESULT = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";


    @DisplayName("문자열을 SHA-256 알고리즘으로 암호화 할 수 있다.")
    @Test
    void encryptWithSHA256() {
        // when
        String encryption = Encryptor.encryptWithSHA256(TEST_STRING);

        // then
        assertThat(encryption).isEqualTo(ENCRYPT_WITH_SHA256_RESULT);
    }
}