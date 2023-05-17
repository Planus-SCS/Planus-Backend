package scs.planus.domain.category.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.category.entity.TodoCategory;

@Getter
@Builder
public class CategoryResponseDto {
    private Long id;

    public static CategoryResponseDto of(TodoCategory todoCategory) {
        return CategoryResponseDto.builder()
                .id(todoCategory.getId())
                .build();
    }
}
