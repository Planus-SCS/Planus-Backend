package scs.planus.global.auth.dto.apple;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class AppleAuthRequestDto {
    @NotBlank(message = "[request] identityToken 값을 입력해 주세요.")
    private String identityToken;
    private FullName fullName;
}
