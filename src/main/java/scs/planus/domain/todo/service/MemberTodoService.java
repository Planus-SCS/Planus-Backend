package scs.planus.domain.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.dto.TodoRequestDto;
import scs.planus.domain.todo.dto.TodoResponseDto;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.entity.Todo;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.domain.todo.repository.TodoRepository;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.util.validator.Validator;

import static scs.planus.global.exception.CustomExceptionStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberTodoService {

    private final MemberRepository memberRepository;
    private final TodoCategoryRepository todoCategoryRepository;
    private final TodoRepository todoRepository;
    private final TodoQueryRepository todoQueryRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public TodoResponseDto createMemberTodo(Long memberId, TodoRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new PlanusException(NONE_USER));

        TodoCategory todoCategory = todoCategoryRepository.findMemberTodoCategoryByIdAndMember(requestDto.getCategoryId(), member)
                .orElseThrow(() -> new PlanusException(NOT_EXIST_CATEGORY));

        Group group = checkAndGetGroup(memberId, requestDto.getGroupId());
        Validator.validateStartDateBeforeEndDate(requestDto.getStartDate(), requestDto.getEndDate());
        MemberTodo memberTodo = requestDto.toMemberTodoEntity(member, todoCategory, group);
        todoRepository.save(memberTodo);
        return TodoResponseDto.of(memberTodo);
    }

    public TodoDetailsResponseDto getOneTodo(Long memberId, Long todoId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Todo todo = todoQueryRepository.findOneMemberTodoById(todoId, member.getId())
                .orElseThrow(() -> new PlanusException(NONE_TODO));

        return TodoDetailsResponseDto.of(todo);
    }

    @Transactional
    public TodoDetailsResponseDto updateTodo(Long memberId, Long todoId, TodoRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Todo todo = todoQueryRepository.findOneMemberTodoById(todoId, member.getId())
                .orElseThrow(() -> new PlanusException(NONE_TODO));

        TodoCategory todoCategory = todoCategoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new PlanusException(NOT_EXIST_CATEGORY));

        Group group = checkAndGetGroup(memberId, requestDto.getGroupId());

        Validator.validateStartDateBeforeEndDate(requestDto.getStartDate(), requestDto.getEndDate());
        todo.update(requestDto.getTitle(), requestDto.getDescription(), requestDto.getStartTime(),
                requestDto.getStartDate(), requestDto.getEndDate(), todoCategory, group);

        return TodoDetailsResponseDto.of(todo);
    }

    @Transactional
    public TodoResponseDto checkCompletion(Long memberId, Long todoId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Todo todo = todoQueryRepository.findOneMemberTodoById(todoId, member.getId())
                .orElseThrow(() -> new PlanusException(NONE_TODO));

        todo.changeCompletion();
        return TodoResponseDto.of(todo);
    }

    @Transactional
    public TodoResponseDto deleteTodo(Long memberId, Long todoId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Todo todo = todoQueryRepository.findOneMemberTodoById(todoId, member.getId())
                .orElseThrow(() -> new PlanusException(NONE_TODO));

        todoRepository.delete(todo);
        return TodoResponseDto.of(todo);
    }

    private Group checkAndGetGroup(Long memberId, Long groupId) {
        if (groupId == null) {
            return null;
        }

        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });
        return groupMember.getGroup();
    }
}
