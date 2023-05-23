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
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.global.exception.CustomExceptionStatus;
import scs.planus.global.exception.PlanusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberTodoCategoryService {
    private final TodoCategoryRepository todoCategoryRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * 카테고리 조회
     */
    public List<TodoCategoryGetResponseDto> findAll(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    throw new PlanusException(CustomExceptionStatus.NONE_USER);
                });

        List<MemberTodoCategory> memberTodoCategories = todoCategoryRepository.findMemberTodoCategoryAllByMember(member);

        return memberTodoCategories.stream()
                .map(TodoCategoryGetResponseDto::of)
                .collect(Collectors.toList());
    }

    /**
     * 속한 그룹 카테고리 조회
     */
    public List<TodoCategoryGetResponseDto> findAllGroupTodoCategories(Long memberId) {

        List<GroupMember> groupMembers = groupMemberRepository.findAllByActiveGroupAndMemberId(memberId);
        List<Group> groups = groupMembers.stream()
                .map(GroupMember::getGroup)
                .collect(Collectors.toList());

        List<GroupTodoCategory> groupTodoCategories = todoCategoryRepository.findAllGroupTodoCategoriesInGroups(groups);
        List<TodoCategoryGetResponseDto> responseDtos = groupTodoCategories.stream()
                .map(TodoCategoryGetResponseDto::of)
                .collect(Collectors.toList());

        return responseDtos;
    }

    /**
     * 새로운 카테고리 생성
     */
    @Transactional
    public TodoCategoryResponseDto createCategory(Long memberId, TodoCategoryRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    throw new PlanusException(CustomExceptionStatus.NONE_USER);
                });

        Color color = Color.of(requestDto.getColor());
        TodoCategory todoCategory = requestDto.toMemberTodoCategoryEntity(member, color);
        TodoCategory saveCategory = todoCategoryRepository.save(todoCategory);

        return TodoCategoryResponseDto.of(saveCategory);
    }

    /**
     * 카테고리 수정
     */
    @Transactional
    public TodoCategoryResponseDto changeCategory(Long categoryId, TodoCategoryRequestDto requestDto) {
        TodoCategory findCategory = todoCategoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    throw new PlanusException(CustomExceptionStatus.NOT_EXIST_CATEGORY);
                });

        Color color = Color.of(requestDto.getColor());
        findCategory.change(requestDto.getName(), color);

        return TodoCategoryResponseDto.of(findCategory);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public TodoCategoryResponseDto deleteCategory(Long categoryId) {
        TodoCategory findCategory = todoCategoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    throw new PlanusException(CustomExceptionStatus.NOT_EXIST_CATEGORY);
                });

        findCategory.changeStatusToInactive();

        return TodoCategoryResponseDto.of(findCategory);
    }
}
