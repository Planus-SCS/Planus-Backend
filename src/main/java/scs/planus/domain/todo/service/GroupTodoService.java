package scs.planus.domain.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.repository.GroupMemberQueryRepository;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.todo.dto.TodoRequestDto;
import scs.planus.domain.todo.dto.TodoResponseDto;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.GroupTodoCompletion;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.domain.todo.repository.TodoRepository;
import scs.planus.global.exception.PlanusException;

import java.util.List;

import static scs.planus.global.exception.CustomExceptionStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupTodoService {

    private final GroupRepository groupRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
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

        List<GroupMember> groupMembers = groupMemberRepository.findAllWithMemberByGroupAndStatus(group);
        groupMembers.stream()
                .map(GroupMember::getMember)
                .forEach(member -> {
                    GroupTodoCompletion.createGroupTodoCompletion(member, groupTodo);
                });

        todoRepository.save(groupTodo);
        return TodoResponseDto.of(groupTodo);
    }
}
