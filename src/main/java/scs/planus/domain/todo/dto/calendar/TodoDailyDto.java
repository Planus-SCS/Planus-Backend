package scs.planus.domain.todo.dto.calendar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.category.entity.Color;
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

    private Boolean hasGroup;
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
                .hasGroup(todo.getGroup() != null)
                .isPeriodTodo(todo.getEndDate().isAfter(todo.getStartDate()))
                .hasDescription(todo.getDescription() != null)
                .isCompleted(todo.isCompletion())
                .build();
    }
}
