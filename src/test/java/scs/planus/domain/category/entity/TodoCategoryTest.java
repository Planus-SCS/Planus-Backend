package scs.planus.domain.category.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.domain.Status;

import static org.assertj.core.api.Assertions.assertThat;

class TodoCategoryTest {
    private TodoCategory todoCategory;

    @BeforeEach
    void TodoCategorySetUP() {
        todoCategory = MemberTodoCategory.builder()
                .name("카테고리1")
                .color(Color.BLUE)
                .build();
    }

    @Test
    @DisplayName("카테고리의 명과 색이 변경되어야 한다.")
    void change() {
        //given
        //when
        todoCategory.change("카테고리2", Color.GOLD);

        //then
        assertThat(todoCategory.getName()).isEqualTo("카테고리2");
        assertThat(todoCategory.getColor()).isEqualTo(Color.GOLD);
    }

    @Test
    @DisplayName("카테고리의 상태(Status)가 Inactive 로 변경되어야 한다.")
    void changeStatusToInactive() {
        //given
        //when
        todoCategory.changeStatusToInactive();

        //then
        assertThat(todoCategory.getStatus()).isEqualTo(Status.INACTIVE);
    }
}