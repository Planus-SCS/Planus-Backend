package scs.planus.domain.todo.controller.calendar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.domain.group.dto.mygroup.GroupBelongInResponseDto;
import scs.planus.domain.todo.dto.calendar.AllTodoResponseDto;
import scs.planus.domain.todo.service.calendar.TodoCalendarService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MemberTodo Calendar", description = "MemberTodo Calendar API Document")
public class MemberTodoCalendarController {

    private final TodoCalendarService todoCalendarService;

    @GetMapping("/todos/calendar")
    @Operation(summary = "월별 Todo Calendar 상세 조회 API - MemberTodo/GroupTodo 전체 조회")
    public BaseResponse<AllTodoResponseDto> getTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        Long memberId = principalDetails.getId();
        AllTodoResponseDto responseDtos = todoCalendarService.getPeriodDetailTodos(memberId, from, to);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("/todos/calendar/my-groups")
    @Operation(summary = "그룹 드롭다운 조회 API")
    public BaseResponse<List<GroupBelongInResponseDto>> getMyGroupsInDropDown(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getId();
        List<GroupBelongInResponseDto> responseDtos = todoCalendarService.getAllMyGroup(memberId);
        return new BaseResponse<>(responseDtos);
    }
}
