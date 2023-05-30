package scs.planus.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.GroupTodoCompletion;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.entity.Todo;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class TodoDetailsResponseDto {

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCompleted;

    public static TodoDetailsResponseDto of(Todo todo) {
        return TodoDetailsResponseDto.builder()
                .todoId(todo.getId())
                .title(todo.getTitle())
                .categoryId(todo.getTodoCategory().getId())
                .groupId(todo.getGroup() == null ? null : todo.getGroup().getId())
                .startDate(todo.getStartDate())
                .endDate(todo.getEndDate())
                .startTime(todo.getStartTime())
                .description(todo.getDescription())
                .isCompleted(((MemberTodo) todo).isCompletion())
                .build();
    }

    public static TodoDetailsResponseDto ofGroupTodo(GroupTodo todo, GroupTodoCompletion completion) {
        return TodoDetailsResponseDto.builder()
                .todoId(todo.getId())
                .title(todo.getTitle())
                .categoryId(todo.getTodoCategory().getId())
                .groupId(todo.getGroup() == null ? null : todo.getGroup().getId())
                .startDate(todo.getStartDate())
                .endDate(todo.getEndDate())
                .startTime(todo.getStartTime())
                .description(todo.getDescription())
                .isCompleted(completion == null ? null : completion.isCompletion())
                .build();
    }
}
