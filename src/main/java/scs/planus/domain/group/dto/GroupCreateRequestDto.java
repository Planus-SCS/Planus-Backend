package scs.planus.domain.group.dto;

import lombok.Getter;
import scs.planus.domain.tag.dto.TagCreateRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Getter
public class GroupCreateRequestDto {

    @NotBlank(message = "[request] 그룹명을 입력해 주세요.")
    @Size(min = 2, max = 20, message = "[request] 그룹명은 2 - 20글자로 입력해 주세요.")
    private String name;

    private String notice;

    @Size(max = 5, message = "[request] 테그는 5개 이하로 입력해 주세요.")
    private List<@Valid TagCreateRequestDto> tagList;

    @NotNull(message = "[request] 그룹 제한 인원을 설정해 주세요.")
    @Max(value = 50, message = "[request] 그룹 제한 인원은 50명 이하로 입력해 주세요.")
    @Min(value = 2, message = "[request] 그룹 제한 인원은 2명 이상으로 입력해 주세요.")
    private int limitCount;
}
