package scs.planus.domain.category.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.entity.TodoCategory;

@Getter
@Builder
public class TodoCategoryForGroupResponseDto {

    private String name;
    private Color color;

    public static TodoCategoryForGroupResponseDto of(TodoCategory todoCategory) {
        return TodoCategoryForGroupResponseDto.builder()
                .name(todoCategory.getName())
                .color(todoCategory.getColor())
                .build();
    }
}
