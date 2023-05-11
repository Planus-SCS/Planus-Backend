package scs.planus.domain.group.dto;

import lombok.Getter;
import scs.planus.domain.tag.dto.TagCreateRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Getter
public class GroupDetailUpdateRequestDto {

    @Size(max = 5, message = "[request] 테그는 5개 이하로 입력해 주세요.")
    private List<@Valid TagCreateRequestDto> tagList;

    @Max(value = 50, message = "[request] 그룹 제한 인원은 50명 이하로 입력해 주세요.")
    @Min(value = 2, message = "[request] 그룹 제한 인원은 2명 이상으로 입력해 주세요.")
    private int limitCount;
}
