package scs.planus.global.auth.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Token {

    private String accessToken;
    private String refreshToken;
    private long refreshTokenExpiredIn;
}
