package scs.planus.domain.category.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.category.entity.TodoCategory;

@Getter
@Builder
public class TodoCategoryResponseDto {
    private Long id;

    public static TodoCategoryResponseDto of(TodoCategory todoCategory) {
        return TodoCategoryResponseDto.builder()
                .id(todoCategory.getId())
                .build();
    }
}
