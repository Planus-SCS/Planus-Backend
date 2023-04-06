package scs.planus.dto.todo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import scs.planus.domain.Member;
import scs.planus.domain.TodoCategory;
import scs.planus.domain.todo.MemberTodo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class TodoCreateRequestDto {

    @NotBlank(message = "투두 제목을 입력해주세요.")
    @Size(max = 20, message = "투두 제목은 최대 20글자입니다.")
    private String title;
    private Long categoryId;
    private Long groupId;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate endDate;
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalTime startTime;

    @Size(max = 70, message = "투두 메모는 최대 70글자입니다.")
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
