package scs.planus.domain.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.category.repository.CategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.dto.TodoCreateRequestDto;
import scs.planus.domain.todo.dto.TodoDailyDto;
import scs.planus.domain.todo.dto.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.TodoDailyScheduleDto;
import scs.planus.domain.todo.dto.TodoGetResponseDto;
import scs.planus.domain.todo.dto.TodoPeriodResponseDto;
import scs.planus.domain.todo.dto.TodoResponseDto;
import scs.planus.domain.todo.entity.Todo;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.domain.todo.repository.TodoRepository;
import scs.planus.global.exception.PlanusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static scs.planus.global.exception.CustomExceptionStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private final TodoRepository todoRepository;
    private final TodoQueryRepository todoQueryRepository;

    @Transactional
    public TodoResponseDto createPrivateTodo(Long memberId, TodoCreateRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new PlanusException(NONE_USER));

        TodoCategory todoCategory = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new PlanusException(NOT_EXIST_CATEGORY));

        validateDate(requestDto.getStartDate(), requestDto.getEndDate());
        Todo memberTodo = requestDto.toMemberTodoEntity(member, todoCategory);
        todoRepository.save(memberTodo);
        return TodoResponseDto.of(memberTodo);
    }

    public TodoGetResponseDto getOneTodo(Long memberId, Long todoId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Todo todo = todoQueryRepository.findOneTodoById(todoId, member.getId())
                .orElseThrow(() -> new PlanusException(NONE_TODO));

        return TodoGetResponseDto.of(todo);
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

    @Transactional
    public TodoGetResponseDto updateTodo(Long memberId, Long todoId, TodoCreateRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Todo todo = todoQueryRepository.findOneTodoById(todoId, member.getId())
                .orElseThrow(() -> new PlanusException(NONE_TODO));

        TodoCategory todoCategory = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new PlanusException(NOT_EXIST_CATEGORY));

        Group group = getGroup(requestDto.getGroupId());

        validateDate(requestDto.getStartDate(), requestDto.getEndDate());
        todo.update(requestDto.getTitle(), requestDto.getDescription(), requestDto.getStartTime(),
                requestDto.getStartDate(), requestDto.getEndDate(), todoCategory, group);

        return TodoGetResponseDto.of(todo);
    }

    @Transactional
    public TodoResponseDto deleteTodo(Long memberId, Long todoId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Todo todo = todoQueryRepository.findOneTodoById(todoId, member.getId())
                .orElseThrow(() -> new PlanusException(NONE_TODO));

        todoRepository.delete(todo);
        return TodoResponseDto.of(todo);
    }

    @Transactional
    public TodoResponseDto checkCompletion(Long memberId, Long todoId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Todo todo = todoQueryRepository.findOneTodoById(todoId, member.getId())
                .orElseThrow(() -> new PlanusException(NONE_TODO));

        todo.complete();
        return TodoResponseDto.of(todo);
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

    private Group getGroup(Long groupId) {
        if (groupId == null) {
            return null;
        }
        // TODO : 그룹 가입 이후, 현재 멤버가 해당 그룹에 가입했는지를 체크해야함. 아래 방식은 가입하지 않더라도 그룹 지정 가능한 방식
        return groupRepository.findById(groupId)
                    .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
    }
}
