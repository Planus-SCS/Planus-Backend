package scs.planus.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.todo.entity.Todo;

import java.time.LocalDate;

@Getter
@Builder
public class TodoPeriodResponseDto {

    private Long todoId;
    private Long categoryId;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate endDate;

    public static TodoPeriodResponseDto of(Todo todo) {
        return TodoPeriodResponseDto.builder()
                .todoId(todo.getId())
                .categoryId(todo.getTodoCategory().getId())
                .title(todo.getTitle())
                .startDate(todo.getStartDate())
                .endDate(todo.getEndDate())
                .build();
    }
}
