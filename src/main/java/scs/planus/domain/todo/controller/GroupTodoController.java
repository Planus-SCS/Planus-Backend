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
import scs.planus.domain.todo.dto.TodoForGroupResponseDto;
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

    @GetMapping("/my-groups/{groupId}/todos/{todoId}")
    @Operation(summary = "단일 Group Todo 조회 API")
    public BaseResponse<TodoForGroupResponseDto> getTodoDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                               @PathVariable Long groupId,
                                                               @PathVariable Long todoId) {
        Long memberId = principalDetails.getId();
        TodoForGroupResponseDto responseDto = groupTodoService.getOneGroupTodo(memberId, groupId, todoId);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/my-groups/{groupId}/members/{memberId}/todos/{todoId}")
    @Operation(summary = "단일 GroupMember Todo 조회 API")
    public BaseResponse<TodoForGroupResponseDto> getGroupMemberTodoDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                          @PathVariable Long groupId,
                                                                          @PathVariable Long memberId,
                                                                          @PathVariable Long todoId) {
        Long loginId = principalDetails.getId();
        TodoForGroupResponseDto responseDto = groupTodoService.getOneGroupMemberTodo(loginId, memberId, groupId, todoId);
        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/my-groups/{groupId}/todos/{todoId}")
    @Operation(summary = "Group Todo 변경 API")
    public BaseResponse<TodoDetailsResponseDto> updateTodoDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                 @PathVariable Long groupId,
                                                                 @PathVariable Long todoId,
                                                                 @Valid @RequestBody TodoRequestDto todoRequestDto) {
        Long memberId = principalDetails.getId();
        TodoDetailsResponseDto responseDto = groupTodoService.updateTodo(memberId, groupId, todoId, todoRequestDto);
        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/my-groups/{groupId}/todos/{todoId}/completion")
    @Operation(summary = "Group Todo 완료 API (GroupTodoCompletion)")
    public BaseResponse<TodoResponseDto> checkCompletion(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @PathVariable Long groupId,
                                                         @PathVariable Long todoId) {
        Long memberId = principalDetails.getId();
        TodoResponseDto responseDto = groupTodoService.checkGroupTodo(memberId, groupId, todoId);
        return new BaseResponse<>(responseDto);
    }

    @DeleteMapping("/my-groups/{groupId}/todos/{todoId}")
    @Operation(summary = "Group Todo 삭제 API")
    public BaseResponse<TodoResponseDto> deleteTodo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable Long groupId,
                                                    @PathVariable Long todoId) {
        Long memberId = principalDetails.getId();
        TodoResponseDto responseDto = groupTodoService.deleteTodo(memberId, groupId, todoId);
        return new BaseResponse<>(responseDto);
    }
}
