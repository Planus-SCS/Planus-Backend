package scs.planus.domain.category.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.category.entity.TodoCategory;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryResponseDto {
    private Long id;

    public CategoryResponseDto(TodoCategory todoCategory) {
        this.id = todoCategory.getId();
    }
}
