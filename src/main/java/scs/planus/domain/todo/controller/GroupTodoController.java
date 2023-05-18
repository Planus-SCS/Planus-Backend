package scs.planus.domain.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.domain.todo.dto.TodoRequestDto;
import scs.planus.domain.todo.dto.TodoResponseDto;
import scs.planus.domain.todo.service.GroupTodoService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "GroupTodo", description = "GroupTodo API Document")
public class GroupTodoController {

    private final GroupTodoService groupTodoService;

    @PostMapping("/my-groups/{groupId}/todos")
    @Operation(summary = "Group Todo 생성 API")
    public BaseResponse<TodoResponseDto> createTodo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable Long groupId,
                                                    @Valid @RequestBody TodoRequestDto todoRequestDto) {
        Long memberId = principalDetails.getId();
        TodoResponseDto responseDto = groupTodoService.createGroupTodo(memberId, groupId, todoRequestDto);
        return new BaseResponse<>(responseDto);
    }
}
