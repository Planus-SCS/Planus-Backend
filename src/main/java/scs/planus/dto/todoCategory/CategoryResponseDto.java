package scs.planus.dto.todoCategory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.TodoCategory;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryResponseDto {
    private Long id;

    public CategoryResponseDto(TodoCategory todoCategory) {
        this.id = todoCategory.getId();
    }
}
