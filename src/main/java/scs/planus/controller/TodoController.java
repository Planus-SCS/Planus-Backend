package scs.planus.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.auth.PrincipalDetails;
import scs.planus.common.response.BaseResponse;
import scs.planus.dto.todo.TodoCreateRequestDto;
import scs.planus.dto.todo.TodoGetResponseDto;
import scs.planus.dto.todo.TodoResponseDto;
import scs.planus.service.TodoService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public BaseResponse<TodoResponseDto> createTodo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @RequestBody TodoCreateRequestDto requestDto) {
        Long memberId = principalDetails.getId();
        if (requestDto.getGroupId() == null) {
            TodoResponseDto responseDto = todoService.createPrivateTodo(memberId, requestDto);
            return new BaseResponse<>(responseDto);
        }
        return null;
    }

    @GetMapping("/todos/{todoId}")
    public BaseResponse<TodoGetResponseDto> getTodoDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @PathVariable Long todoId) {
        Long memberId = principalDetails.getId();
        TodoGetResponseDto responseDto = todoService.getOneTodo(memberId, todoId);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/todos")
    public BaseResponse<List<TodoGetResponseDto>> getDailyTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long memberId = principalDetails.getId();
        List<TodoGetResponseDto> responseDtos = todoService.getDailyTodos(memberId, date);
        return new BaseResponse<>(responseDtos);
    }
}
