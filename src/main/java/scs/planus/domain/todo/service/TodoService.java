package scs.planus.domain.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.global.exception.PlanusException;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.todo.entity.Todo;
import scs.planus.domain.todo.dto.TodoCreateRequestDto;
import scs.planus.domain.todo.dto.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.TodoGetResponseDto;
import scs.planus.domain.todo.dto.TodoResponseDto;
import scs.planus.domain.category.repository.CategoryRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.domain.todo.repository.TodoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<TodoDailyResponseDto> getDailyTodos(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        List<Todo> todos = todoQueryRepository.findDailyTodosByDate(member.getId(), date);
        List<TodoDailyResponseDto> responseDtos = todos.stream()
                .map(TodoDailyResponseDto::of)
                .collect(Collectors.toList());

        return responseDtos;
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

    private void validateDate(LocalDate startDate, LocalDate endDate) {
        if (endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new PlanusException(INVALID_DATE);
            }
        }
    }

    private Group getGroup(Long groupId) {
        if (groupId == null) {
            return null;
        }
        // TODO : 그룹 가입 이후, 현재 멤버가 해당 그룹에 가입했는지를 체크해야함. 아래 방식은 가입하지 않더라도 그룹 지정 가능한 방식
        return groupRepository.findById(groupId)
                    .orElseThrow(() -> new PlanusException(NOT_EXIST_CATEGORY));
    }
}
