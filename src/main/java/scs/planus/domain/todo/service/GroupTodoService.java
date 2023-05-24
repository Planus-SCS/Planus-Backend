package scs.planus.domain.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.dto.TodoForGroupResponseDto;
import scs.planus.domain.todo.dto.TodoRequestDto;
import scs.planus.domain.todo.dto.TodoResponseDto;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.GroupTodoCompletion;
import scs.planus.domain.todo.entity.Todo;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.domain.todo.repository.TodoRepository;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.util.validator.Validator;

import java.util.List;

import static scs.planus.global.exception.CustomExceptionStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupTodoService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final TodoCategoryRepository todoCategoryRepository;
    private final TodoRepository todoRepository;
    private final TodoQueryRepository todoQueryRepository;

    @Transactional
    public TodoResponseDto createGroupTodo(Long memberId, Long groupId, TodoRequestDto requestDto) {
        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        Group group = groupMember.getGroup();
        boolean hasTodoAuthority = groupMember.isTodoAuthority();

        if (!hasTodoAuthority) {
            throw new PlanusException(DO_NOT_HAVE_TODO_AUTHORITY);
        }

        GroupTodoCategory groupTodoCategory = todoCategoryRepository.findGroupTodoCategoryByIdAndStatus(requestDto.getCategoryId())
                .orElseThrow(() -> new PlanusException(NOT_EXIST_CATEGORY));

        GroupTodo groupTodo = requestDto.toGroupTodoEntity(group, groupTodoCategory);

        // TODO 연관된 다른 메서드들이 많아 @Param을 group으로 진행 -> 이후 일관성있도록 리펙토링 필요
        List<GroupMember> groupMembers = groupMemberRepository.findAllWithMemberByGroupAndStatus(group);
        groupMembers.stream()
                .map(GroupMember::getMember)
                .forEach(member -> {
                    GroupTodoCompletion.createGroupTodoCompletion(member, groupTodo);
                });

        todoRepository.save(groupTodo);
        return TodoResponseDto.of(groupTodo);
    }

    public TodoDetailsResponseDto getOneGroupTodo(Long memberId, Long groupId, Long todoId) {
        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        GroupTodo groupTodo = todoQueryRepository.findOneGroupTodoById(groupId, todoId)
                .orElseThrow(() -> new PlanusException(NONE_TODO));
        return TodoForGroupResponseDto.of(groupTodo);
    }

    public TodoDetailsResponseDto getOneGroupMemberTodo(Long loginId, Long memberId, Long groupId, Long todoId) {
        GroupMember loginMember = groupMemberRepository.findByMemberIdAndGroupId(loginId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new PlanusException(NOT_JOINED_MEMBER_IN_GROUP));

        Todo todo = todoQueryRepository.findOneGroupMemberTodoById(groupId, memberId, todoId)
                .orElseThrow(() -> new PlanusException(NONE_TODO));
        return TodoForGroupResponseDto.of(todo);
    }

    @Transactional
    public TodoDetailsResponseDto updateTodo(Long memberId, Long groupId, Long todoId, TodoRequestDto requestDto) {
        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        Group group = groupMember.getGroup();
        boolean hasTodoAuthority = groupMember.isTodoAuthority();

        if (!hasTodoAuthority) {
            throw new PlanusException(DO_NOT_HAVE_TODO_AUTHORITY);
        }

        GroupTodo groupTodo = todoQueryRepository.findOneGroupTodoById(groupId, todoId)
                .orElseThrow(() -> new PlanusException(NONE_TODO));

        GroupTodoCategory groupTodoCategory = todoCategoryRepository.findGroupTodoCategoryByIdAndStatus(requestDto.getCategoryId())
                .orElseThrow(() -> new PlanusException(NOT_EXIST_CATEGORY));

        Validator.validateStartDateBeforeEndDate(requestDto.getStartDate(), requestDto.getEndDate());
        groupTodo.update(requestDto.getTitle(), requestDto.getDescription(), requestDto.getStartTime(),
                requestDto.getStartDate(), requestDto.getEndDate(), groupTodoCategory, group);

        return TodoDetailsResponseDto.of(groupTodo);
    }

    @Transactional
    public TodoResponseDto deleteTodo(Long memberId, Long groupId, Long todoId) {
        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        boolean hasTodoAuthority = groupMember.isTodoAuthority();

        if (!hasTodoAuthority) {
            throw new PlanusException(DO_NOT_HAVE_TODO_AUTHORITY);
        }

        GroupTodo groupTodo = todoQueryRepository.findOneGroupTodoById(groupId, todoId)
                .orElseThrow(() -> new PlanusException(NONE_TODO));

        todoRepository.delete(groupTodo);
        return TodoResponseDto.of(groupTodo);
    }
}
