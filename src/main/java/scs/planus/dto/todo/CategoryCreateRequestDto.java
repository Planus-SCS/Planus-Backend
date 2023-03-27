package scs.planus.dto.todo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.Color;
import scs.planus.domain.TodoCategory;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryCreateRequestDto {
    private String name;
    private Color color;

    @Builder
    public CategoryCreateRequestDto(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public TodoCategory toEntity() {
        return TodoCategory.builder()
                .name(this.name)
                .color(this.color)
                .build();
    }

}
