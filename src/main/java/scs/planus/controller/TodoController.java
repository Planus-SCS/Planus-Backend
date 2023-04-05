package scs.planus.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.auth.PrincipalDetails;
import scs.planus.common.response.BaseResponse;
import scs.planus.dto.todo.TodoCreateRequestDto;
import scs.planus.dto.todo.TodoResponseDto;
import scs.planus.service.TodoService;

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
        return null; // 그룹개인투두 미구현
    }
}
