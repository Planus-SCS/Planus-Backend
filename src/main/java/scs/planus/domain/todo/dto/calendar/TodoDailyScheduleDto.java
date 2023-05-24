package scs.planus.domain.todo.dto.calendar;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.todo.entity.Todo;

import java.time.LocalTime;

@Getter
@Builder
public class TodoDailyScheduleDto {

    private Long todoId;
    private String categoryColor;
    private String title;
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalTime startTime;

    private Boolean hasGroup;
    private Boolean isPeriodTodo;
    private Boolean hasDescription;
    private Boolean isCompleted;

    public static TodoDailyScheduleDto of(Todo todo) {
        return TodoDailyScheduleDto.builder()
                .todoId(todo.getId())
                .categoryColor(todo.getTodoCategory().getColor().toString())
                .title(todo.getTitle())
                .startTime(todo.getStartTime())
                .hasGroup(todo.getGroup() != null)
                .isPeriodTodo(todo.getEndDate().isAfter(todo.getStartDate()))
                .hasDescription(todo.getDescription() != null)
                .isCompleted(todo.isCompletion())
                .build();
    }
}
