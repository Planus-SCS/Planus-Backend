package scs.planus.domain.category.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.member.entity.Member;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryRequestDto {
    @NotBlank(message = "[request] 제목을 입력해 주세요.")
    @Size(min = 1, max = 10, message = "[request] 제목은 1 ~ 10 글자로 입력해 주세요.")
    private String name;

    @NotBlank(message = "[request] 색을 지정해 주세요.")
    private String color;

    public MemberTodoCategory toEntity(Member member) {
        Color color = Color.isValid(this.color);
        return MemberTodoCategory.builder()
                .member(member)
                .name(this.name)
                .color(color)
                .build();
    }
}
