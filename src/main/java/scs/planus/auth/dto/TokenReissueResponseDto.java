package scs.planus.auth.dto;

import lombok.Getter;
import scs.planus.auth.jwt.Token;

@Getter
public class TokenReissueResponseDto {

    private final String accessToken;
    private final String refreshToken;

    public TokenReissueResponseDto(Token token) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
    }
}
