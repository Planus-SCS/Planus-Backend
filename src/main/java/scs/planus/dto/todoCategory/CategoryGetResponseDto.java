package scs.planus.dto.todoCategory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.Color;
import scs.planus.domain.TodoCategory;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryGetResponseDto {
    private Long id;
    private String name;
    private Color color;

    public CategoryGetResponseDto(TodoCategory todoCategory) {
        this.id = todoCategory.getId();
        this.name = todoCategory.getName();
        this.color = todoCategory.getColor();
    }
}
