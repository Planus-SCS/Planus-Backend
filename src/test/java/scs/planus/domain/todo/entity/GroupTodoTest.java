package scs.planus.domain.todo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.group.entity.Group;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class GroupTodoTest {

    @DisplayName("그룹 투두 변경이 제대로 이루어져야 한다.")
    @Test
    void update_Success(){
        //given
        LocalTime time = LocalTime.of(11, 0, 0);
        LocalDate date = LocalDate.of(2023, 1, 1);

        GroupTodo groupTodo = GroupTodo.builder()
                .title("title")
                .description("desc")
                .startTime(time)
                .startDate(date)
                .build();

        //when
        Group group = Group.builder().build();
        TodoCategory groupTodoCategory = GroupTodoCategory.builder().build();

        groupTodo.update("newTitle", "newDesc",
                time.plusMinutes(5), date.plusDays(2), date.plusDays(5),
                groupTodoCategory, group);

        //then
        assertThat(groupTodo.getTitle()).isEqualTo("newTitle");
        assertThat(groupTodo.getDescription()).isEqualTo("newDesc");
        assertThat(groupTodo.getStartTime()).isEqualTo(time.plusMinutes(5));
        assertThat(groupTodo.getStartDate()).isEqualTo(date.plusDays(2));
        assertThat(groupTodo.getEndDate()).isEqualTo(date.plusDays(5));
        assertThat(groupTodo.getGroup()).isEqualTo(group);
        assertThat(groupTodo.getTodoCategory()).isEqualTo(groupTodoCategory);
    }
}