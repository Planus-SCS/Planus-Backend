package scs.planus.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.MemberTodo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoRequestDto {

    @NotBlank(message = "투두 제목을 입력해주세요.")
    @Size(max = 20, message = "투두 제목은 최대 20글자입니다.")
    private String title;
    private Long categoryId;
    private Long groupId;

    @NotNull(message = "시작 날짜를 선택해주세요.")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate endDate;

    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    @Schema(defaultValue = "23:30")
    private LocalTime startTime;

    @Size(max = 70, message = "투두 메모는 최대 70글자입니다.")
    private String description;

    public MemberTodo toMemberTodoEntity(Member member, TodoCategory todoCategory, Group group) {
        return MemberTodo.builder()
                .title(title)
                .todoCategory(todoCategory)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .description(description)
                .member(member)
                .group(group)
                .isGroupTodo(false)
                .build();
    }

    public GroupTodo toGroupTodoEntity(Group group, TodoCategory todoCategory) {
        return GroupTodo.builder()
                .title(title)
                .todoCategory(todoCategory)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .description(description)
                .group(group)
                .isGroupTodo(true)
                .build();
    }
}
