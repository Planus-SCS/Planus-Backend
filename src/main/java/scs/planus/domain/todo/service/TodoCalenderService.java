package scs.planus.domain.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.dto.TodoDailyDto;
import scs.planus.domain.todo.dto.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.TodoDailyScheduleDto;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.dto.TodoPeriodResponseDto;
import scs.planus.domain.todo.entity.Todo;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.global.exception.PlanusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static scs.planus.global.exception.CustomExceptionStatus.INVALID_DATE;
import static scs.planus.global.exception.CustomExceptionStatus.NONE_USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TodoCalenderService {

    private final MemberRepository memberRepository;
    private final TodoQueryRepository todoQueryRepository;

    public List<TodoDetailsResponseDto> getPeriodDetailTodos(Long memberId, LocalDate from, LocalDate to) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        validateDate(from, to);
        List<Todo> todos = todoQueryRepository.findPeriodTodosDetailByDate(member.getId(), from, to);
        List<TodoDetailsResponseDto> responseDtos = todos.stream()
                .map(TodoDetailsResponseDto::of)
                .collect(Collectors.toList());

        return responseDtos;
    }

    public List<TodoPeriodResponseDto> getPeriodTodos(Long memberId, LocalDate from, LocalDate to) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        validateDate(from, to);
        List<Todo> todos = todoQueryRepository.findPeriodTodosByDate(member.getId(), from, to);
        List<TodoPeriodResponseDto> responseDtos = todos.stream()
                .map(TodoPeriodResponseDto::of)
                .collect(Collectors.toList());
        return responseDtos;
    }

    public TodoDailyResponseDto getDailyTodos(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        List<Todo> todos = todoQueryRepository.findDailyTodosByDate(member.getId(), date);
        List<TodoDailyScheduleDto> todoDailyScheduleDtos = getDailySchedules(todos);
        List<TodoDailyDto> todoDailyDtos = getDailyTodos(todos);

        return TodoDailyResponseDto.of(todoDailyScheduleDtos, todoDailyDtos);
    }

    private void validateDate(LocalDate startDate, LocalDate endDate) {
        if (endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new PlanusException(INVALID_DATE);
            }
        }
    }

    private List<TodoDailyScheduleDto> getDailySchedules(List<Todo> todos) {
        return todos.stream()
                .filter(todo -> todo.getStartTime() != null)
                .map(TodoDailyScheduleDto::of)
                .collect(Collectors.toList());
    }

    private List<TodoDailyDto> getDailyTodos(List<Todo> todos) {
        return todos.stream()
                .filter(todo -> todo.getStartTime() == null)
                .map(TodoDailyDto::of)
                .collect(Collectors.toList());
    }
}
