package scs.planus.dto.todo;

import lombok.*;
import scs.planus.common.exception.PlanusException;
import scs.planus.common.response.CommonResponseStatus;
import scs.planus.domain.Color;
import scs.planus.domain.Member;
import scs.planus.domain.TodoCategory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryCreateRequestDto {
    @NotBlank(message = "[request] 제목을 입력해 주세요.")
    @Size(min = 1, max = 10, message = "[request] 제목은 1 ~ 10 글자로 입력해 주세요.")
    private String name;

    @NotBlank(message = "[request] 색을 지정해 주세요.")
    private String color;

    public TodoCategory toEntity(Member member) {
        Color color = validateColor(this.color);
        return TodoCategory.builder()
                .member(member)
                .name(this.name)
                .color(color)
                .build();
    }

    public TodoCategory toEntity() {
        Color color = validateColor(this.color);
        return TodoCategory.builder()
                .name(this.name)
                .color(color)
                .build();
    }

    private Color validateColor(String color) throws PlanusException {
        for (Color enumColor : Color.values()) {
            if (enumColor.name().equals(color)) {
                return enumColor;
            }
        }
        throw new PlanusException(CommonResponseStatus.INVALID_PARAMETER);
    }

}
