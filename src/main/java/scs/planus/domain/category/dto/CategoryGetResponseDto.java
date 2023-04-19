package scs.planus.domain.category.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.entity.TodoCategory;

@Getter
@Builder
public class CategoryGetResponseDto {
    private Long id;
    private String name;
    private Color color;

    public static CategoryGetResponseDto of(TodoCategory todoCategory) {
        return CategoryGetResponseDto.builder()
                .id(todoCategory.getId())
                .name(todoCategory.getName())
                .color(todoCategory.getColor())
                .build();
    }
}
