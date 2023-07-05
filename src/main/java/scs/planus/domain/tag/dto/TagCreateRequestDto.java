package scs.planus.domain.tag.dto;

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
public class TagCreateRequestDto {
    @NotBlank(message = "[request] 태그명을 입력해 주세요.")
    @Size(max = 7, message = "[request] 태그는 7 글자 이내로 작성해 주세요.")
    private String name;
}