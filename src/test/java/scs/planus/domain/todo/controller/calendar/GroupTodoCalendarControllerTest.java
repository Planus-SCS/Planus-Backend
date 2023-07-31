package scs.planus.domain.todo.controller.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.todo.dto.calendar.TodoDailyDto;
import scs.planus.domain.todo.dto.calendar.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.calendar.TodoPeriodResponseDto;
import scs.planus.domain.todo.service.calendar.GroupTodoCalendarService;
import scs.planus.support.ControllerTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupTodoCalendarController.class)
class GroupTodoCalendarControllerTest extends ControllerTest {

    @MockBean
    private GroupTodoCalendarService groupTodoCalendarService;

    @Nested
    @DisplayName("일별 Todo 조회")
    class DailyTodos {

        private String date;
        private List<TodoDailyDto> dailySchedules;
        private List<TodoDailyDto> dailyTodos;
        private TodoDailyResponseDto dailyResponseDto;

        @BeforeEach
        void init() {
            date = LocalDate.of(2023, 1, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            dailySchedules = List.of(TodoDailyDto.builder()
                    .todoId(1L)
                    .categoryColor(Color.BLUE)
                    .title("dailySchedule")
                    .isGroupTodo(true)
                    .startTime(LocalTime.now())
                    .build());

            dailyTodos = List.of(TodoDailyDto.builder()
                    .todoId(2L)
                    .categoryColor(Color.RED)
                    .title("dailyTodo")
                    .isGroupTodo(true)
                    .build());

            dailyResponseDto = TodoDailyResponseDto.of(dailySchedules, dailyTodos);
        }

        @DisplayName("특정 일의 GroupTodo들을 조회한다.")
        @Test
        void getDailyTodos() throws Exception {
            //given
            String path = "/app/my-groups/{groupId}/todos/calendar/daily";
            Long groupId = 1L;

            given(groupTodoCalendarService.getDailyGroupTodos(anyLong(), anyLong(), any(LocalDate.class)))
                    .willReturn(dailyResponseDto);

            //when & then
            mockMvc
                    .perform(get(path, groupId)
                            .param("date", date)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("특정 일의 GroupMember의 Todo들을 조회한다.")
        @Test
        void getGroupMemberDailyTodos() throws Exception {
            //given
            String path = "/app/my-groups/{groupId}/members/{memberId}/calendar/daily";
            Long groupId = 1L;
            Long memberId = 1L;

            given(groupTodoCalendarService.getGroupMemberDailyTodos(anyLong(), anyLong(), anyLong(), any(LocalDate.class)))
                    .willReturn(dailyResponseDto);
            //when & then
            mockMvc
                    .perform(get(path, groupId, memberId)
                            .param("date", date)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("기간별 Todo 조회")
    class PeriodTodos {

        private String from;
        private String to;
        private List<TodoPeriodResponseDto> responseDtos;

        @BeforeEach
        void init() {
            from = LocalDate.of(2023, 1, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            to = LocalDate.of(2023, 1, 31).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            responseDtos = List.of(TodoPeriodResponseDto.builder()
                    .todoId(1L)
                    .categoryColor(Color.RED)
                    .title("groupTodo")
                    .startDate(LocalDate.of(2023, 1, 15))
                    .build());
        }

        @DisplayName("특정 기간내의 GroupTodo들을 조회한다.")
        @Test
        void getPeriodTodos() throws Exception {
            //given
            String path = "/app/my-groups/{groupId}/todos/calendar";
            Long groupId = 1L;

            given(groupTodoCalendarService.getPeriodGroupTodos(anyLong(), anyLong(), any(LocalDate.class), any(LocalDate.class)))
                    .willReturn(responseDtos);

            //when & then
            mockMvc
                    .perform(get(path, groupId)
                            .param("from", from)
                            .param("to", to)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }



        @DisplayName("특정 기간내의 GroupMember의 Todo들을 조회한다.")
        @Test
        void getGroupMemberPeriodTodos() throws Exception {
            //given
            String path = "/app/my-groups/{groupId}/members/{memberId}/calendar";
            Long groupId = 1L;
            Long memberId = 1L;

            given(groupTodoCalendarService.getGroupMemberPeriodTodos(anyLong(), anyLong(), anyLong(), any(LocalDate.class), any(LocalDate.class)))
                    .willReturn(responseDtos);

            //when & then
            mockMvc
                    .perform(get(path, groupId, memberId)
                            .param("from", from)
                            .param("to", to)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}