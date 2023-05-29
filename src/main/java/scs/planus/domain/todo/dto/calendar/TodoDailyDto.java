package scs.planus.domain.todo.dto.calendar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.GroupTodoCompletion;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.entity.Todo;

import java.time.LocalTime;

@Getter
@Builder
public class TodoDailyDto {

    private Long todoId;
    private Color categoryColor;
    private String title;

    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalTime startTime;

    private Boolean isGroupTodo;
    private Boolean isPeriodTodo;
    private Boolean hasDescription;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCompleted;

    public static TodoDailyDto of(Todo todo) {
        return TodoDailyDto.builder()
                .todoId(todo.getId())
                .categoryColor(todo.getTodoCategory().getColor())
                .title(todo.getTitle())
                .startTime(todo.getStartTime())
                .isGroupTodo(todo.isGroupTodo())
                .isPeriodTodo(todo.getEndDate().isAfter(todo.getStartDate()))
                .hasDescription(todo.getDescription() != null)
                .isCompleted(((MemberTodo)todo).isCompletion())
                .build();
    }

    public static TodoDailyDto ofGroupTodo(GroupTodo todo) {
        return TodoDailyDto.builder()
                .todoId(todo.getId())
                .categoryColor(todo.getTodoCategory().getColor())
                .title(todo.getTitle())
                .startTime(todo.getStartTime())
                .isGroupTodo(todo.isGroupTodo())
                .isPeriodTodo(todo.getEndDate().isAfter(todo.getStartDate()))
                .hasDescription(todo.getDescription() != null)
                .build();
    }

    public static TodoDailyDto ofGroupTodo(GroupTodo todo, GroupTodoCompletion completion) {
        return TodoDailyDto.builder()
                .todoId(todo.getId())
                .categoryColor(todo.getTodoCategory().getColor())
                .title(todo.getTitle())
                .startTime(todo.getStartTime())
                .isGroupTodo(todo.isGroupTodo())
                .isPeriodTodo(todo.getEndDate().isAfter(todo.getStartDate()))
                .hasDescription(todo.getDescription() != null)
                .isCompleted(completion == null ? null : completion.isCompletion())
                .build();
    }
}
