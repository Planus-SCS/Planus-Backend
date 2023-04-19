package scs.planus.domain.todo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;
import scs.planus.domain.todo.dto.TodoCreateRequestDto;
import scs.planus.domain.todo.dto.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.TodoGetResponseDto;
import scs.planus.domain.todo.dto.TodoResponseDto;
import scs.planus.domain.todo.service.TodoService;

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
    public BaseResponse<List<TodoDailyResponseDto>> getDailyTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long memberId = principalDetails.getId();
        List<TodoDailyResponseDto> responseDtos = todoService.getDailyTodos(memberId, date);
        return new BaseResponse<>(responseDtos);
    }

    @PatchMapping("/todos/{todoId}")
    public BaseResponse<TodoGetResponseDto> updateTodoDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                             @PathVariable Long todoId,
                                                             @RequestBody TodoCreateRequestDto requestDto) {
        Long memberId = principalDetails.getId();
        TodoGetResponseDto responseDto = todoService.updateTodo(memberId, todoId, requestDto);
        return new BaseResponse<>(responseDto);
    }

    @DeleteMapping("/todos/{todoId}")
    public BaseResponse<TodoResponseDto> deleteTodo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable Long todoId) {
        Long memberId = principalDetails.getId();
        TodoResponseDto responseDto = todoService.deleteTodo(memberId, todoId);
        return new BaseResponse<>(responseDto);
    }
}
