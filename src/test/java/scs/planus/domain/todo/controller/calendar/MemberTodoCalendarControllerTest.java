package scs.planus.domain.todo.controller.calendar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import scs.planus.domain.group.dto.mygroup.GroupBelongInResponseDto;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.dto.calendar.AllTodoResponseDto;
import scs.planus.domain.todo.service.calendar.MemberTodoCalendarService;
import scs.planus.support.ControllerTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberTodoCalendarController.class)
class MemberTodoCalendarControllerTest extends ControllerTest {

    @MockBean
    private MemberTodoCalendarService memberTodoCalendarService;

    @DisplayName("특정 기간내의 해당 멤버와 관련된 Todo들을 조회한다.")
    @Test
    void getTodos() throws Exception {
        //given
        String path = "/app/todos/calendar";
        String from = LocalDate.of(2023, 1, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String to = LocalDate.of(2023, 1, 31).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        TodoDetailsResponseDto memberTodo = TodoDetailsResponseDto.builder()
                .todoId(1L)
                .title("memberTodo")
                .categoryId(1L)
                .startDate(LocalDate.of(2023,1,1))
                .description("memberTodo Description")
                .build();

        TodoDetailsResponseDto groupTodo = TodoDetailsResponseDto.builder()
                .todoId(2L)
                .title("groupTodo")
                .categoryId(2L)
                .groupId(1L)
                .startDate(LocalDate.of(2023,1,15))
                .description("groupTodo Description")
                .build();

        AllTodoResponseDto responseDto = AllTodoResponseDto.builder()
                .memberTodos(List.of(memberTodo))
                .groupTodos(List.of(groupTodo))
                .build();

        //when
        given(memberTodoCalendarService.getPeriodDetailTodos(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(responseDto);

        //then
        mockMvc
                .perform(get(path)
                        .param("from", from)
                        .param("to", to)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("멤버가 속한 모든 Group를 조회한다.")
    @Test
    void getMyGroupsInDropDown() throws Exception {
        //given
        String path = "/app/todos/calendar/my-groups";

        List<GroupBelongInResponseDto> responseDtos = List.of(GroupBelongInResponseDto.builder()
                .groupId(1L)
                .groupName("group1")
                .build());

        //when
        given(memberTodoCalendarService.getAllMyGroup(anyLong()))
                .willReturn(responseDtos);

        //then
        mockMvc
                .perform(get(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}