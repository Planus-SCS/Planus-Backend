package scs.planus.infra.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import scs.planus.global.auth.entity.Token;
import scs.planus.support.ServiceTest;

import static org.assertj.core.api.Assertions.assertThat;

class RedisServiceTest extends ServiceTest {

    private static final String TEST_EMAIL = "test@test";

    private final RedisTemplate<?, ?> redisTemplate;
    private final RedisService redisService;
    private Token token;

    @Autowired
    public RedisServiceTest(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        redisService = new RedisService(redisTemplate);
    }

    @BeforeEach
    void init() {
        token = Token.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .refreshTokenExpiredIn(10L)
                .build();
    }

    @DisplayName("(email-refreshToken) 쌍으로 Redis에 제대로 저장되고 조회되어야 한다.")
    @Test
    void saveAndGetValue() {
        //when
        redisService.saveValue(TEST_EMAIL, token);

        //then
        String value = redisService.getValue(TEST_EMAIL);
        assertThat(value).isEqualTo(token.getRefreshToken());
    }

    @DisplayName("키를 통해 Redis에 존재하는 값을 제거한다.")
    @Test
    void delete(){
        //given
        redisService.saveValue(TEST_EMAIL, token);

        //when
        redisService.delete(TEST_EMAIL);

        //then
        assertThat(redisService.getValue(TEST_EMAIL)).isNull();
    }
}