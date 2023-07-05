package scs.planus.domain.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberUpdateRequestDto {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2-10 글자로 입력해주세요.")
    private String nickname;

    @Size(max = 50, message = "자기 소개는 최대 50자까지 작성할 수 있습니다.")
    private String description;

    private boolean profileImageRemove;
}
