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

    private Long id;
    private String title;
    private String categoryName;
    private String groupName;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate endDate;
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalTime startTime;
    private String description;

    public static TodoGetResponseDto of(Todo todo) {
        return TodoGetResponseDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .categoryName(todo.getTodoCategory().getName())
                .groupName(todo.getGroup() == null ? null : todo.getGroup().getName())
                .startDate(todo.getStartDate())
                .endDate(todo.getEndDate())
                .startTime(todo.getStartTime())
                .description(todo.getDescription())
                .build();
    }
}
