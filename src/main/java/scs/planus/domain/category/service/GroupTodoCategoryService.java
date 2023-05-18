package scs.planus.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.category.dto.TodoCategoryGetResponseDto;
import scs.planus.domain.category.dto.TodoCategoryRequestDto;
import scs.planus.domain.category.dto.TodoCategoryResponseDto;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.global.exception.PlanusException;

import java.util.List;
import java.util.stream.Collectors;

import static scs.planus.global.exception.CustomExceptionStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupTodoCategoryService {

    private final TodoCategoryRepository todoCategoryRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public List<TodoCategoryGetResponseDto> findAll(Long memberId, Long groupId) {
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

        List<GroupTodoCategory> groupTodoCategories = todoCategoryRepository.findGroupTodoCategoryAllByGroup(groupId);

        return groupTodoCategories.stream()
                .map(TodoCategoryGetResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public TodoCategoryResponseDto createCategory(Long memberId, Long groupId, TodoCategoryRequestDto requestDto) {
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

        Color color = Color.of(requestDto.getColor());

        GroupTodoCategory groupTodoCategory = requestDto.toGroupTodoCategoryEntity(group, color);
        GroupTodoCategory saveGroupTodoCategory = todoCategoryRepository.save(groupTodoCategory);

        return TodoCategoryResponseDto.of(saveGroupTodoCategory);
    }

    @Transactional
    public TodoCategoryResponseDto changeCategory(Long memberId, Long groupId, Long categoryId, TodoCategoryRequestDto requestDto) {
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

        GroupTodoCategory groupTodoCategory = todoCategoryRepository.findGroupTodoCategoryByIdAndStatus(categoryId)
                .orElseThrow(() -> new PlanusException(NOT_EXIST_CATEGORY));

        Color color = Color.of(requestDto.getColor());
        groupTodoCategory.change(requestDto.getName(), color);

        return TodoCategoryResponseDto.of(groupTodoCategory);
    }

    @Transactional
    public TodoCategoryResponseDto deleteCategory(Long memberId, Long groupId, Long categoryId) {
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

        GroupTodoCategory groupTodoCategory = todoCategoryRepository.findGroupTodoCategoryByIdAndStatus(categoryId)
                .orElseThrow(() -> new PlanusException(NOT_EXIST_CATEGORY));

        groupTodoCategory.changeStatusToInactive();

        return TodoCategoryResponseDto.of(groupTodoCategory);
    }
}
