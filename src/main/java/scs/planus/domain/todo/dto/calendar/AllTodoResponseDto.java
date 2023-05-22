package scs.planus.domain.todo.dto.calendar;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;

import java.util.List;

@Getter
@Builder
public class AllTodoResponseDto {

    private List<TodoDetailsResponseDto> memberTodos;
    private List<TodoDetailsResponseDto> groupTodos;

    public static AllTodoResponseDto of(List<TodoDetailsResponseDto> memberTodos, List<TodoDetailsResponseDto> groupTodos) {
        return AllTodoResponseDto.builder()
                .memberTodos(memberTodos)
                .groupTodos(groupTodos)
                .build();
    }
}
