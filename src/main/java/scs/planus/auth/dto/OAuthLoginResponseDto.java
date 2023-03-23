package scs.planus.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthLoginResponseDto {

    private Long memberId;
    private String accessToken;
    private String refreshToken;
}
