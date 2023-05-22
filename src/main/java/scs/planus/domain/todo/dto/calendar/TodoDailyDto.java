package scs.planus.domain.todo.dto.calendar;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.todo.entity.Todo;

@Getter
@Builder
public class TodoDailyDto {

    private Long todoId;
    private Long categoryId;
    private String title;

    private Boolean hasGroup;
    private Boolean isPeriodTodo;
    private Boolean hasDescription;
    private Boolean isCompleted;

    public static TodoDailyDto of(Todo todo) {
        return TodoDailyDto.builder()
                .todoId(todo.getId())
                .categoryId(todo.getTodoCategory().getId())
                .title(todo.getTitle())
                .hasGroup(todo.getGroup() != null)
                .isPeriodTodo(todo.getEndDate().isAfter(todo.getStartDate()))
                .hasDescription(todo.getDescription() != null)
                .isCompleted(todo.isCompletion())
                .build();
    }
}