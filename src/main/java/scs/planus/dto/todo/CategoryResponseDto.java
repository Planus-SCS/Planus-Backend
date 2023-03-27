package scs.planus.dto.todo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.Color;
import scs.planus.domain.TodoCategory;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryResponseDto {
    private String name;
    private Color color;

    public CategoryResponseDto(TodoCategory todoCategory) {
        this.name = todoCategory.getName();
        this.color = todoCategory.getColor();
    }
}
