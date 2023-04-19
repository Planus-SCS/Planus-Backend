package scs.planus.domain.todo.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.todo.entity.Todo;

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
