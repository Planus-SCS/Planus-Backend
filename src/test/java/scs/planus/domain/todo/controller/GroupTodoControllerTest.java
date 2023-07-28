package scs.planus.domain.todo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import scs.planus.domain.todo.dto.TodoForGroupResponseDto;
import scs.planus.domain.todo.dto.TodoRequestDto;
import scs.planus.domain.todo.dto.TodoResponseDto;
import scs.planus.domain.todo.service.GroupTodoService;
import scs.planus.support.ControllerTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupTodoController.class)
class GroupTodoControllerTest extends ControllerTest {

    @MockBean
    private GroupTodoService groupTodoService;

    @DisplayName("GroupTodo를 생성한다.")
    @Test
    void createTodo() throws Exception {
        //given
        String path = "/app/my-groups/{groupId}/todos";
        Long groupId = 1L;

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("todo title")
                .categoryId(1L)
                .groupId(groupId)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .startTime(LocalTime.now())
                .description("todo description")
                .build();

        given(groupTodoService.createGroupTodo(anyLong(), anyLong(), any(TodoRequestDto.class)))
                .willReturn(TodoResponseDto.builder().todoId(1L).build());

        //when & then
        mockMvc
                .perform(post(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("GroupTodo 생성시, 검증조건을 만족하지 못하면 예외를 던진다.")
    @Test
    void createGroupTodo_Throw_Exception_If_Not_Validated_Request() throws Exception {
        //given
        String path = "/app/my-groups/{groupId}/todos";
        Long groupId = 1L;

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("A".repeat(21))
                .categoryId(1L)
                .groupId(groupId)
                .startDate(null)
                .endDate(LocalDate.now())
                .startTime(LocalTime.now())
                .description("A".repeat(71))
                .build();

        given(groupTodoService.createGroupTodo(anyLong(), anyLong(),any(TodoRequestDto.class)))
                .willReturn(TodoResponseDto.builder().todoId(1L).build());

        //when & then
        mockMvc
                .perform(post(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("단일 GroupTodo를 조회한다.")
    @Test
    void getTodoDetail() throws Exception {
        //given
        String path = "/app/my-groups/{groupId}/todos/{todoId}";
        Long groupId = 1L;
        Long todoId = 1L;

        TodoForGroupResponseDto responseDto = TodoForGroupResponseDto.builder()
                .todoId(todoId)
                .title("groupTodo")
                .groupName("planus")
                .startDate(LocalDate.now())
                .startTime(LocalTime.now())
                .description("groupTodo Description")
                .build();

        given(groupTodoService.getOneGroupTodo(anyLong(), anyLong(), anyLong()))
                .willReturn(responseDto);

        //when & then
        mockMvc
                .perform(get(path, groupId, todoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("단일 GroupMemberTodo를 조회한다.")
    @Test
    void getGroupMemberTodoDetail() throws Exception {
        //given
        String path = "/app/my-groups/{groupId}/members/{memberId}/todos/{todoId}";
        Long groupId = 1L;
        Long memberId = 1L;
        Long todoId = 1L;

        TodoForGroupResponseDto responseDto = TodoForGroupResponseDto.builder()
                .todoId(todoId)
                .title("groupMemberTodo")
                .groupName("planus")
                .startDate(LocalDate.now())
                .startTime(LocalTime.now())
                .description("groupMemberTodo Description")
                .build();

        given(groupTodoService.getOneGroupMemberTodo(anyLong(),anyLong(), anyLong(), anyLong()))
                .willReturn(responseDto);

        //when & then
        mockMvc
                .perform(get(path, groupId, memberId, todoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("GroupTodo를 수정한다.")
    @Test
    void updateTodoDetail() throws Exception {
        //given
        String path = "/app/my-groups/{groupId}/todos/{todoId}";
        Long groupId = 1L;
        Long todoId = 1L;

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("todo title")
                .categoryId(1L)
                .groupId(groupId)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .startTime(LocalTime.now())
                .description("todo description")
                .build();

        given(groupTodoService.updateTodo(anyLong(),anyLong(), anyLong(), any(TodoRequestDto.class)))
                .willReturn(TodoResponseDto.builder().todoId(todoId).build());

        //when & then
        mockMvc
                .perform(patch(path, groupId, todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("GroupTodo의 completion을 변경한다.")
    @Test
    void checkCompletion() throws Exception {
        //given
        String path = "/app/my-groups/{groupId}/todos/{todoId}/completion";
        Long groupId = 1L;
        Long todoId = 1L;

        given(groupTodoService.checkGroupTodo(anyLong(),anyLong(), anyLong()))
                .willReturn(TodoResponseDto.builder().todoId(todoId).build());

        //when & then
        mockMvc
                .perform(patch(path, groupId, todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("GroupTodo를 삭제한다.")
    @Test
    void deleteTodo() throws Exception {
        //given
        String path = "/app/my-groups/{groupId}/todos/{todoId}";
        Long groupId = 1L;
        Long todoId = 1L;

        given(groupTodoService.deleteTodo(anyLong(),anyLong(), anyLong()))
                .willReturn(TodoResponseDto.builder().todoId(todoId).build());

        //when & then
        mockMvc
                .perform(delete(path, groupId, todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}