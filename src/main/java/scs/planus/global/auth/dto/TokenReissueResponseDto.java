package scs.planus.global.auth.dto;

import lombok.Getter;
import scs.planus.global.auth.entity.Token;

@Getter
public class TokenReissueResponseDto {

    private final String accessToken;
    private final String refreshToken;

    public TokenReissueResponseDto(Token token) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
    }
}
