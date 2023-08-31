package scs.planus.global.auth.dto.apple;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppleAuthRequestDto {
    @NotBlank(message = "[request] identityToken 값을 입력해 주세요.")
    private String identityToken;
    private FullName fullName;
}
