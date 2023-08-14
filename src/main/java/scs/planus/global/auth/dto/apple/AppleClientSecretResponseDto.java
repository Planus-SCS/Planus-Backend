package scs.planus.global.auth.dto.apple;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppleClientSecretResponseDto {
    private String clientSecret;
}
