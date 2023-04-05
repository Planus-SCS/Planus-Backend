package scs.planus.dto.todo;

import lombok.Getter;
import scs.planus.domain.Member;
import scs.planus.domain.TodoCategory;
import scs.planus.domain.todo.MemberTodo;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class TodoCreateRequestDto {

    private String title;
    private Long categoryId;
    private Long groupId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private String description;

    public MemberTodo toMemberTodoEntity(Member member, TodoCategory todoCategory) {
        return MemberTodo.builder()
                .title(title)
                .todoCategory(todoCategory)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .description(description)
                .member(member)
                .build();
    }
}
