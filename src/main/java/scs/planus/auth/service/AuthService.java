package scs.planus.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.auth.dto.TokenReissueRequestDto;
import scs.planus.auth.dto.TokenReissueResponseDto;
import scs.planus.auth.jwt.JwtProvider;
import scs.planus.auth.jwt.Token;
import scs.planus.auth.jwt.redis.RedisService;
import scs.planus.common.exception.PlanusException;

import static scs.planus.common.response.CustomResponseStatus.EXPIRED_REFRESH_TOKEN;
import static scs.planus.common.response.CustomResponseStatus.INVALID_REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final RedisService redisService;
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenReissueResponseDto reissue(TokenReissueRequestDto requestDto) {
        String oldAccessToken = requestDto.getAccessToken();
        String oldRefreshToken = requestDto.getRefreshToken();
        String email = jwtProvider.getPayload(oldAccessToken);
        validateRefreshToken(email, oldRefreshToken);

        Token token = jwtProvider.generateToken(email);
        redisService.saveValue(email, token);

        return new TokenReissueResponseDto(token);
    }

    private void validateRefreshToken(String email, String refreshToken) {
        if (redisService.getValue(email) == null) {
            throw new PlanusException(EXPIRED_REFRESH_TOKEN);
        }

        if (!redisService.getValue(email).equals(refreshToken)) {
            throw new PlanusException(INVALID_REFRESH_TOKEN);
        }
    }
}
