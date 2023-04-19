package scs.planus.domain.tag.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class TagCreateRequestDto {
    @NotBlank(message = "[request] 태그명을 입력해 주세요.")
    @Size(max = 7, message = "[request] 태그는 7 글자 이내로 작성해 주세요.")
    private String name;
}