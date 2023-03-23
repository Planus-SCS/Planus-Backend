package scs.planus.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthLoginResponseDto {

    private String email;
    private String nickname;
}
