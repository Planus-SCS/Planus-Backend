package scs.planus.domain.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.dto.TodoRequestDto;
import scs.planus.domain.todo.dto.TodoResponseDto;
import scs.planus.domain.todo.service.TodoService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Todo", description = "Todo API Document")
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    @Operation(summary = "Todo 생성 API")
    public BaseResponse<TodoResponseDto> createTodo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @RequestBody TodoRequestDto todoRequestDto) {
        Long memberId = principalDetails.getId();
        TodoResponseDto responseDto = todoService.createPrivateTodo(memberId, todoRequestDto);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/todos/{todoId}")
    @Operation(summary = "Todo 조회 API")
    public BaseResponse<TodoDetailsResponseDto> getTodoDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                              @PathVariable Long todoId) {
        Long memberId = principalDetails.getId();
        TodoDetailsResponseDto responseDto = todoService.getOneTodo(memberId, todoId);
        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/todos/{todoId}")
    @Operation(summary = "Todo 변경 API")
    public BaseResponse<TodoDetailsResponseDto> updateTodoDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                 @PathVariable Long todoId,
                                                                 @RequestBody TodoRequestDto todoRequestDto) {
        Long memberId = principalDetails.getId();
        TodoDetailsResponseDto responseDto = todoService.updateTodo(memberId, todoId, todoRequestDto);
        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/todos/{todoId}/completion")
    @Operation(summary = "Todo 완료 API")
    public BaseResponse<TodoResponseDto> completeTodo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @PathVariable Long todoId) {
        Long memberId = principalDetails.getId();
        TodoResponseDto responseDto = todoService.checkCompletion(memberId, todoId);
        return new BaseResponse<>(responseDto);
    }

    @DeleteMapping("/todos/{todoId}")
    @Operation(summary = "Todo 삭제 API")
    public BaseResponse<TodoResponseDto> deleteTodo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable Long todoId) {
        Long memberId = principalDetails.getId();
        TodoResponseDto responseDto = todoService.deleteTodo(memberId, todoId);
        return new BaseResponse<>(responseDto);
    }
}
