package scs.planus.domain.todo.dto.calendar;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TodoDailyResponseDto {

    private List<TodoDailyScheduleDto> dailySchedules;
    private List<TodoDailyDto> dailyTodos;

    public static TodoDailyResponseDto of(List<TodoDailyScheduleDto> todoDailyScheduleDtos,
                                          List<TodoDailyDto> todoDailyDtos) {
        return TodoDailyResponseDto.builder()
                .dailySchedules(todoDailyScheduleDtos)
                .dailyTodos(todoDailyDtos)
                .build();
    }
}
