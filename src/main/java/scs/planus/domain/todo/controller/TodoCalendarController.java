package scs.planus.domain.todo.controller;

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
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.service.TodoCalendarService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Todo Calendar", description = "Todo Calendar API Document")
public class TodoCalendarController {

    private final TodoCalendarService todoCalendarService;

    @GetMapping("/todos/calendar")
    @Operation(summary = "월별 Todo Calendar 상세 조회 API - Todo 내용 전체 출력")
    public BaseResponse<List<TodoDetailsResponseDto>> getTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        Long memberId = principalDetails.getId();
        List<TodoDetailsResponseDto> responseDtos = todoCalendarService.getPeriodDetailTodos(memberId, from, to);
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
