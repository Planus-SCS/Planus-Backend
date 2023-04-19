package scs.planus.domain.member.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class MemberUpdateRequestDto {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2-10 글자로 입력해주세요.")
    private String nickname;

    @Size(max = 50, message = "자기 소개는 최대 50자까지 작성할 수 있습니다.")
    private String description;
}
