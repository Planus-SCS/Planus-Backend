package scs.planus.domain.todo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.domain.todo.dto.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.dto.TodoPeriodResponseDto;
import scs.planus.domain.todo.service.TodoCalenderService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
public class TodoCalenderController {

    private final TodoCalenderService todoCalenderService;

    @GetMapping("/todos")
    public BaseResponse<List<TodoDetailsResponseDto>> getTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to){
        Long memberId = principalDetails.getId();
        List<TodoDetailsResponseDto> responseDtos = todoCalenderService.getPeriodDetailTodos(memberId, from, to);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("/todos/period")
    public BaseResponse<List<TodoPeriodResponseDto>> getPeriodTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        Long memberId = principalDetails.getId();
        List<TodoPeriodResponseDto> responseDtos = todoCalenderService.getPeriodTodos(memberId, from, to);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("/todos/daily")
    public BaseResponse<TodoDailyResponseDto> getDailyTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long memberId = principalDetails.getId();
        TodoDailyResponseDto responseDtos = todoCalenderService.getDailyTodos(memberId, date);
        return new BaseResponse<>(responseDtos);
    }
}
