package scs.planus.domain.todo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.group.entity.Group;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTodoTest {

    private MemberTodo todo;
    
    @DisplayName("endDate를 입력하지 않을 시, startDate와 동일한 값으로 초기화되어야 한다.")
    @Test
    void endDate_Is_Same_As_StartDate_When_EndDate_Is_Null(){
        //given
        LocalDate date = LocalDate.of(2023, 1, 1);

        //when
        todo = MemberTodo.builder()
                .title("title")
                .startDate(date)
                .build();

        //then
        assertThat(todo.getEndDate()).isEqualTo(date);
    }

    @DisplayName("투두 변경이 제대로 이루어져야 한다.")
    @Test
    void update_Success(){
        //given
        LocalTime time = LocalTime.of(11, 0, 0);
        LocalDate date = LocalDate.of(2023, 1, 1);

        todo = MemberTodo.builder()
                .title("title")
                .description("desc")
                .startTime(time)
                .startDate(date)
                .build();

        //when
        Group group = Group.builder().build();
        TodoCategory todoCategory = MemberTodoCategory.builder().build();

        todo.update("newTitle", "newDesc",
                time.plusMinutes(5), date.plusDays(2), date.plusDays(5),
                todoCategory, group);

        //then
        assertThat(todo.getTitle()).isEqualTo("newTitle");
        assertThat(todo.getDescription()).isEqualTo("newDesc");
        assertThat(todo.getStartTime()).isEqualTo(time.plusMinutes(5));
        assertThat(todo.getStartDate()).isEqualTo(date.plusDays(2));
        assertThat(todo.getEndDate()).isEqualTo(date.plusDays(5));
        assertThat(todo.getTodoCategory()).isEqualTo(todoCategory);
        assertThat(todo.getGroup()).isEqualTo(group);
    }

    @DisplayName("투두 완료 상태를 true에서 false로 변경할 수 있어야 한다.")
    @Test
    void changeCompletion_From_True_To_False(){
        //given
        todo = MemberTodo.builder()
                .completion(true)
                .build();

        //when
        todo.changeCompletion();

        //then
        assertThat(todo.isCompletion()).isFalse();
    }

    @DisplayName("투두 완료 상태를 false에서 true로 변경할 수 있어야 한다.")
    @Test
    void changeCompletion_From_False_To_True(){
        //given
        todo = MemberTodo.builder()
                .completion(false)
                .build();

        //when
        todo.changeCompletion();

        //then
        assertThat(todo.isCompletion()).isTrue();
    }
}