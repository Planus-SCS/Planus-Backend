package scs.planus.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.todo.entity.Todo;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class TodoGetResponseDto {

    private Long todoId;
    private String title;
    private Long categoryId;
    private Long groupId;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate endDate;
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalTime startTime;
    private String description;

    public static TodoGetResponseDto of(Todo todo) {
        return TodoGetResponseDto.builder()
                .todoId(todo.getId())
                .title(todo.getTitle())
                .categoryId(todo.getTodoCategory().getId())
                .groupId(todo.getGroup() == null ? null : todo.getGroup().getId())
                .startDate(todo.getStartDate())
                .endDate(todo.getEndDate())
                .startTime(todo.getStartTime())
                .description(todo.getDescription())
                .build();
    }
}
