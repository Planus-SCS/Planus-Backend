package scs.planus.auth.dto;

import lombok.Getter;

@Getter
public class GoogleTokenResponseDto extends OAuth2TokenResponseDto {

    private String scope;
}
