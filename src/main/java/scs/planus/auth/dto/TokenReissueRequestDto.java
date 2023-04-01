package scs.planus.auth.dto;

import lombok.Getter;

@Getter
public class TokenReissueRequestDto {

    private String accessToken;
    private String refreshToken;
}
