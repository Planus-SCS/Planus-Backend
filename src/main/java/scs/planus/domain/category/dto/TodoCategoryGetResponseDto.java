package scs.planus.domain.category.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.Status;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.entity.TodoCategory;

@Getter
@Builder
public class TodoCategoryGetResponseDto {
    private Long id;
    private String name;
    private Color color;
    private Status status;

    public static TodoCategoryGetResponseDto of(TodoCategory todoCategory) {
        return TodoCategoryGetResponseDto.builder()
                .id(todoCategory.getId())
                .name(todoCategory.getName())
                .color(todoCategory.getColor())
                .status(todoCategory.getStatus())
                .build();
    }
}
