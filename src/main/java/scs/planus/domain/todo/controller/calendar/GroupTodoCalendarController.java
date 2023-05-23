package scs.planus.domain.todo.controller.calendar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.domain.todo.dto.calendar.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.calendar.TodoPeriodResponseDto;
import scs.planus.domain.todo.service.calendar.GroupTodoCalendarService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "GroupTodo Calendar", description = "GroupTodo Calendar API Document")
public class GroupTodoCalendarController {

    private final GroupTodoCalendarService groupTodoCalendarService;

    @GetMapping("my-groups/{groupId}/todos/calendar")
    @Operation(summary = "월별 GroupTodo Calendar 조회 API")
    public BaseResponse<List<TodoPeriodResponseDto>> getPeriodTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                    @PathVariable Long groupId,
                                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        Long memberId = principalDetails.getId();
        List<TodoPeriodResponseDto> responseDtos = groupTodoCalendarService.getPeriodGroupTodos(memberId, groupId, from, to);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("my-groups/{groupId}/todos/calendar/daily")
    @Operation(summary = "일별 GroupTodo/GroupSchedule 조회 API")
    public BaseResponse<TodoDailyResponseDto> getDailyTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @PathVariable Long groupId,
                                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long memberId = principalDetails.getId();
        TodoDailyResponseDto responseDtos = groupTodoCalendarService.getDailyGroupTodos(memberId, groupId, date);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("/my-groups/{groupId}/members/{memberId}/calendar")
    @Operation(summary = "월별 GroupMemberTodo Calendar 조회 API")
    public BaseResponse<List<TodoPeriodResponseDto>> getGroupMemberPeriodTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                               @PathVariable Long groupId,
                                                                               @PathVariable Long memberId,
                                                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        Long loginId = principalDetails.getId();
        List<TodoPeriodResponseDto> responseDtos = groupTodoCalendarService.getGroupMemberPeriodTodos(loginId, groupId, memberId, from, to);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("/my-groups/{groupId}/members/{memberId}/calendar/daily")
    @Operation(summary = "일별 GroupMemberTodo/GroupMemberSchedule 조회 API")
    public BaseResponse<TodoDailyResponseDto> getGroupMemberPeriodTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                        @PathVariable Long groupId,
                                                                        @PathVariable Long memberId,
                                                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long loginId = principalDetails.getId();
        TodoDailyResponseDto responseDtos = groupTodoCalendarService.getGroupMemberDailyTodos(loginId, groupId, memberId, date);
        return new BaseResponse<>(responseDtos);
    }
}
