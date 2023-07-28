package scs.planus.domain.todo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.dto.TodoRequestDto;
import scs.planus.domain.todo.dto.TodoResponseDto;
import scs.planus.domain.todo.service.MemberTodoService;
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

@WebMvcTest(MemberTodoController.class)
class MemberTodoControllerTest extends ControllerTest {

    @MockBean
    private MemberTodoService memberTodoService;

    @DisplayName("MemberTodo가 생성되어야 한다.")
    @Test
    void createTodo() throws Exception {
        //given
        String path = "/app/todos";
        
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("todo title")
                .categoryId(1L)
                .groupId(1L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .startTime(LocalTime.now())
                .description("todo description")
                .build();

        given(memberTodoService.createMemberTodo(anyLong(), any(TodoRequestDto.class)))
                .willReturn(TodoResponseDto.builder().todoId(1L).build());

        //when & then
        mockMvc
                .perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("MemberTodo 생성시, 검증조건을 만족하지 못하면 예외를 던진다.")
    @Test
    void createTodo_Throw_Exception_If_Not_Validated_Request() throws Exception {
        //given
        String path = "/app/todos";
        
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("A".repeat(21))
                .categoryId(1L)
                .groupId(1L)
                .startDate(null)
                .endDate(LocalDate.now())
                .startTime(LocalTime.now())
                .description("A".repeat(71))
                .build();

        given(memberTodoService.createMemberTodo(anyLong(), any(TodoRequestDto.class)))
                .willReturn(TodoResponseDto.builder().todoId(1L).build());

        //when & then
        mockMvc
                .perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("MemberTodo가 조회되어야 한다.")
    @Test
    void getTodoDetail() throws Exception {
        //given
        String path = "/app/todos/{todoId}";
        Long todoId = 1L;

        given(memberTodoService.getOneTodo(anyLong(), anyLong()))
                .willReturn(TodoDetailsResponseDto.builder()
                        .todoId(todoId)
                        .title("todo title")
                        .categoryId(1L)
                        .groupId(1L)
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now())
                        .description("todo description")
                        .isCompleted(false)
                        .build());

        //when & then
        mockMvc
                .perform(get(path, todoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("MemberTodo가 변경되어야 한다.")
    @Test
    void updateTodoDetail() throws Exception {
        //given
        String path = "/app/todos/{todoId}";
        Long todoId = 1L;

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("todo title")
                .categoryId(1L)
                .groupId(1L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .startTime(LocalTime.now())
                .description("todo description")
                .build();

        given(memberTodoService.updateTodo(anyLong(), anyLong(), any(TodoRequestDto.class)))
                .willReturn(TodoResponseDto.builder().todoId(todoId).build());

        //when & then
        mockMvc
                .perform(patch(path, todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("MemberTodo의 completion을 변경한다.")
    @Test
    void completeTodo() throws Exception {
        //given
        String path = "/app/todos/{todoId}/completion";
        Long todoId = 1L;

        given(memberTodoService.checkCompletion(anyLong(), anyLong()))
                .willReturn(TodoResponseDto.builder().todoId(todoId).build());

        //when & then
        mockMvc
                .perform(patch(path, todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("MemberTodo를 삭제한다.")
    @Test
    void deleteTodo() throws Exception {
        //given
        String path = "/app/todos/{todoId}";
        Long todoId = 1L;

        given(memberTodoService.deleteTodo(anyLong(), anyLong()))
                .willReturn(TodoResponseDto.builder().todoId(todoId).build());

        //when & then
        mockMvc
                .perform(delete(path, todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}