package scs.planus.dto.todo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import scs.planus.domain.todo.Todo;

import java.time.LocalTime;

@Getter
@Builder
@ToString
public class TodoGetResponseDto {

    private Long todoId;
    private String title;
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalTime startTime;

    public static TodoGetResponseDto of(Todo todo) {
        return TodoGetResponseDto.builder()
                .todoId(todo.getId())
                .title(todo.getTitle())
                .startTime(todo.getStartTime())
                .build();
    }
}
