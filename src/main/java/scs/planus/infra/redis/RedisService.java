package scs.planus.infra.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import scs.planus.global.auth.entity.Token;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveValue(String email, Token token) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String refreshToken = token.getRefreshToken();
        Duration expired = Duration.ofMillis(token.getRefreshTokenExpiredIn());
        valueOperations.set(email, refreshToken, expired);
    }

    public String getValue(String email){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(email);
    }

    public void delete(String email) {
        redisTemplate.delete(email);
    }
}
