package scs.planus.domain.category.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.entity.TodoCategory;

@Getter
@Builder
public class TodoCategorySummaryResponseDto {

    private String name;
    private Color color;

    public static TodoCategorySummaryResponseDto of(TodoCategory todoCategory) {
        return TodoCategorySummaryResponseDto.builder()
                .name(todoCategory.getName())
                .color(todoCategory.getColor())
                .build();
    }
}
