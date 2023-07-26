package scs.planus.domain.category.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.entity.TodoCategory;

@Getter
@Builder
public class TodoCategoryForGroupResponseDto {

    private Long todoCategoryId;
    private String name;
    private Color color;

    public static TodoCategoryForGroupResponseDto of(TodoCategory todoCategory) {
        return TodoCategoryForGroupResponseDto.builder()
                .todoCategoryId(todoCategory.getId())
                .name(todoCategory.getName())
                .color(todoCategory.getColor())
                .build();
    }
}
