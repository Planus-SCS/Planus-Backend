package scs.planus.global.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OAuthTokenResponseDto {

    @JsonProperty("access_token")
    private String accessToken;

}
