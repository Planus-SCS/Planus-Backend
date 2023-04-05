package scs.planus.dto.todo;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.todo.Todo;

@Getter
@Builder
public class TodoResponseDto {

    private Long todoId;

    public static TodoResponseDto of(Todo todo) {
        return TodoResponseDto.builder()
                .todoId(todo.getId())
                .build();
    }
}
