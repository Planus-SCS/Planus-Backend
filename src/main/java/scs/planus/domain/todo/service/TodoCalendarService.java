package scs.planus.domain.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.group.dto.mygroup.GroupBelongInResponseDto;
import scs.planus.domain.group.repository.GroupMemberQueryRepository;
import scs.planus.domain.group.service.MyGroupService;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.dto.TodoDailyDto;
import scs.planus.domain.todo.dto.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.TodoDailyScheduleDto;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.dto.TodoPeriodResponseDto;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.util.validator.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static scs.planus.global.exception.CustomExceptionStatus.NONE_USER;
import static scs.planus.global.exception.CustomExceptionStatus.NOT_JOINED_GROUP;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TodoCalendarService {

    private final MyGroupService myGroupService;
    private final MemberRepository memberRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final TodoQueryRepository todoQueryRepository;

    public List<TodoDetailsResponseDto> getPeriodDetailTodos(Long memberId, LocalDate from, LocalDate to) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Validator.validateStartDateBeforeEndDate(from, to);
        List<MemberTodo> todos = todoQueryRepository.findPeriodTodosByDate(member.getId(), from, to);
        List<TodoDetailsResponseDto> responseDtos = todos.stream()
                .map(TodoDetailsResponseDto::of)
                .collect(Collectors.toList());

        return responseDtos;
    }

    public List<TodoPeriodResponseDto> getPeriodTodos(Long memberId, LocalDate from, LocalDate to) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Validator.validateStartDateBeforeEndDate(from, to);
        List<MemberTodo> todos = todoQueryRepository.findPeriodTodosByDate(member.getId(), from, to);
        List<TodoPeriodResponseDto> responseDtos = todos.stream()
                .map(TodoPeriodResponseDto::of)
                .collect(Collectors.toList());
        return responseDtos;
    }

    public TodoDailyResponseDto getDailyTodos(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        List<MemberTodo> todos = todoQueryRepository.findDailyTodosByDate(member.getId(), date);
        List<TodoDailyScheduleDto> todoDailyScheduleDtos = getDailySchedules(todos);
        List<TodoDailyDto> todoDailyDtos = getDailyTodos(todos);

        return TodoDailyResponseDto.of(todoDailyScheduleDtos, todoDailyDtos);
    }

    public List<GroupBelongInResponseDto> getAllMyGroup(Long memberId) {
        List<GroupBelongInResponseDto> responseDtos = myGroupService.getMyGroupsInDropDown(memberId);
        return responseDtos;
    }

    public List<TodoDetailsResponseDto> getPeriodDetailGroupTodos(Long memberId, Long groupId, LocalDate from, LocalDate to) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        boolean isJoined = groupMemberQueryRepository.existByMemberIdAndGroupId(member.getId(), groupId);

        if (!isJoined) {
            throw new PlanusException(NOT_JOINED_GROUP);
        }

        Validator.validateStartDateBeforeEndDate(from, to);
        List<MemberTodo> todos = todoQueryRepository.findPeriodGroupTodosByDate(member.getId(), groupId, from, to);
        List<TodoDetailsResponseDto> responseDtos = todos.stream()
                .map(TodoDetailsResponseDto::of)
                .collect(Collectors.toList());

        return responseDtos;
    }

    public List<TodoPeriodResponseDto> getPeriodGroupTodos(Long memberId, Long groupId, LocalDate from, LocalDate to) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        boolean isJoined = groupMemberQueryRepository.existByMemberIdAndGroupId(member.getId(), groupId);

        if (!isJoined) {
            throw new PlanusException(NOT_JOINED_GROUP);
        }

        Validator.validateStartDateBeforeEndDate(from, to);
        List<MemberTodo> todos = todoQueryRepository.findPeriodGroupTodosByDate(member.getId(), groupId, from, to);
        List<TodoPeriodResponseDto> responseDtos = todos.stream()
                .map(TodoPeriodResponseDto::of)
                .collect(Collectors.toList());

        return responseDtos;
    }

    private List<TodoDailyScheduleDto> getDailySchedules(List<MemberTodo> todos) {
        return todos.stream()
                .filter(todo -> todo.getStartTime() != null)
                .map(TodoDailyScheduleDto::of)
                .collect(Collectors.toList());
    }

    private List<TodoDailyDto> getDailyTodos(List<MemberTodo> todos) {
        return todos.stream()
                .filter(todo -> todo.getStartTime() == null)
                .map(TodoDailyDto::of)
                .collect(Collectors.toList());
    }
}
