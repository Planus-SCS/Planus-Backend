package scs.planus.domain.todo.service.calendar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.todo.dto.calendar.TodoDailyDto;
import scs.planus.domain.todo.dto.calendar.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.calendar.TodoPeriodResponseDto;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.GroupTodoCompletion;
import scs.planus.domain.todo.entity.Todo;
import scs.planus.domain.todo.repository.GroupTodoCompletionRepository;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.util.validator.Validator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static scs.planus.global.exception.CustomExceptionStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupTodoCalendarService {

    private final TodoQueryRepository todoQueryRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupTodoCompletionRepository groupTodoCompletionRepository;

    public List<TodoPeriodResponseDto> getPeriodGroupTodos(Long memberId, Long groupId, LocalDate from, LocalDate to) {
        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        Validator.validateStartDateBeforeEndDate(from, to);

        List<GroupTodo> todos = todoQueryRepository.findPeriodGroupTodosByDate(groupId, from, to);
        List<TodoPeriodResponseDto> responseDtos = todos.stream()
                .map(TodoPeriodResponseDto::of)
                .collect(Collectors.toList());
        return responseDtos;
    }

    public TodoDailyResponseDto getDailyGroupTodos(Long memberId, Long groupId, LocalDate date) {
        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        List<GroupTodo> todos = todoQueryRepository.findDailyGroupTodosByDate(groupId, date);
        List<TodoDailyDto> todoDailyScheduleDtos = getDailyGroupSchedules(todos);
        List<TodoDailyDto> todoDailyDtos = getDailyGroupTodos(todos);

        return TodoDailyResponseDto.of(todoDailyScheduleDtos, todoDailyDtos);
    }

    public List<TodoPeriodResponseDto> getGroupMemberPeriodTodos(Long loginId, Long groupId, Long memberId,
                                                                  LocalDate from, LocalDate to) {
        GroupMember loginGroupMember = groupMemberRepository.findByMemberIdAndGroupId(loginId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new PlanusException(NOT_JOINED_MEMBER_IN_GROUP));

        Validator.validateStartDateBeforeEndDate(from, to);
        List<Todo> todos = todoQueryRepository.findGroupMemberPeriodTodosByDate(memberId, groupId, from, to);

        List<TodoPeriodResponseDto> responseDtos = todos.stream()
                .map(TodoPeriodResponseDto::of)
                .collect(Collectors.toList());
        return responseDtos;
    }

    public TodoDailyResponseDto getGroupMemberDailyTodos(Long loginId, Long groupId, Long memberId, LocalDate date) {
        GroupMember loginGroupMember = groupMemberRepository.findByMemberIdAndGroupId(loginId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new PlanusException(NOT_JOINED_MEMBER_IN_GROUP));

        List<Todo> todos = todoQueryRepository.findGroupMemberDailyTodosByDate(memberId, groupId, date);

        List<GroupTodoCompletion> groupTodoCompletions = groupTodoCompletionRepository.findAllByMemberIdOnGroupId(memberId, groupId);

        List<TodoDailyDto> allTodos = getAllGroupMemberTodos(todos, groupTodoCompletions);

        List<TodoDailyDto> dailySchedules = allTodos.stream()
                .filter(todoDailyDto -> todoDailyDto.getStartTime() != null)
                .sorted(Comparator.comparing(TodoDailyDto::getStartTime))
                .collect(Collectors.toList());

        List<TodoDailyDto> dailyTodos = allTodos.stream()
                .filter(todoDailyDto -> todoDailyDto.getStartTime() == null)
                .sorted(Comparator.comparing(TodoDailyDto::getTodoId))
                .collect(Collectors.toList());

        return TodoDailyResponseDto.of(dailySchedules, dailyTodos);
    }

    private List<TodoDailyDto> getDailyGroupSchedules(List<GroupTodo> todos) {
        return todos.stream()
                .filter(todo -> todo.getStartTime() != null)
                .map(TodoDailyDto::ofGroupTodo)
                .collect(Collectors.toList());
    }

    private List<TodoDailyDto> getDailyGroupTodos(List<GroupTodo> todos) {
        return todos.stream()
                .filter(todo -> todo.getStartTime() == null)
                .map(TodoDailyDto::ofGroupTodo)
                .collect(Collectors.toList());
    }

    private List<TodoDailyDto> getAllGroupMemberTodos(List<Todo> todos, List<GroupTodoCompletion> groupTodoCompletions) {
        return todos.stream()
                .map(todo -> {
                    if (!todo.isGroupTodo()) {
                        return TodoDailyDto.of(todo);
                    }
                    GroupTodoCompletion todoCompletion = groupTodoCompletions.stream()
                            .filter(groupTodoCompletion -> groupTodoCompletion.getGroupTodo().equals(todo))
                            .findFirst().orElse(null);
                    return TodoDailyDto.ofGroupTodo((GroupTodo) todo, todoCompletion);
                })
                .collect(Collectors.toList());
    }
}
