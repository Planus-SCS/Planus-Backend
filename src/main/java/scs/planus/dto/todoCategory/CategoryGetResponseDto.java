package scs.planus.dto.todoCategory;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.Color;
import scs.planus.domain.TodoCategory;

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
