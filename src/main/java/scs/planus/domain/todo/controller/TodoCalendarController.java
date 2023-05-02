package scs.planus.domain.todo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.domain.group.dto.GroupBelongInResponseDto;
import scs.planus.domain.todo.dto.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.dto.TodoPeriodResponseDto;
import scs.planus.domain.todo.service.TodoCalendarService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
public class TodoCalendarController {

    private final TodoCalendarService todoCalendarService;

    @GetMapping("/todos/calendar")
    public BaseResponse<List<TodoDetailsResponseDto>> getTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to){
        Long memberId = principalDetails.getId();
        List<TodoDetailsResponseDto> responseDtos = todoCalendarService.getPeriodDetailTodos(memberId, from, to);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("/todos//calendar/period")
    public BaseResponse<List<TodoPeriodResponseDto>> getPeriodTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        Long memberId = principalDetails.getId();
        List<TodoPeriodResponseDto> responseDtos = todoCalendarService.getPeriodTodos(memberId, from, to);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("/todos/calendar/daily")
    public BaseResponse<TodoDailyResponseDto> getDailyTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long memberId = principalDetails.getId();
        TodoDailyResponseDto responseDtos = todoCalendarService.getDailyTodos(memberId, date);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("/todos/calendar/my-groups")
    public BaseResponse<List<GroupBelongInResponseDto>> getMyGroups(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getId();
        List<GroupBelongInResponseDto> responseDtos = todoCalendarService.getAllMyGroup(memberId);
        return new BaseResponse<>(responseDtos);
    }
}
