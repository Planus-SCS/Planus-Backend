package scs.planus.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoTokenResponseDto extends OAuth2TokenResponseDto{
    @JsonProperty("refresh_token_expires_in")
    private String refreshTokenExpiresIn;
}
