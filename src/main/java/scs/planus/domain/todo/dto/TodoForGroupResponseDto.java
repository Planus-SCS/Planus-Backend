package scs.planus.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.category.dto.TodoCategoryForGroupResponseDto;
import scs.planus.domain.todo.entity.Todo;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class TodoForGroupResponseDto {

    private Long todoId;
    private String title;
    private TodoCategoryForGroupResponseDto todoCategory;
    private String groupName;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate endDate;
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalTime startTime;
    private String description;

    public static TodoForGroupResponseDto of(Todo todo) {
        return TodoForGroupResponseDto.builder()
                .todoId(todo.getId())
                .title(todo.getTitle())
                .todoCategory(TodoCategoryForGroupResponseDto.of(todo.getTodoCategory()))
                .groupName(todo.getGroup().getName())
                .startDate(todo.getStartDate())
                .endDate(todo.getEndDate())
                .startTime(todo.getStartTime())
                .description(todo.getDescription())
                .build();
    }
}
