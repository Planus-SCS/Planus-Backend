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
import scs.planus.domain.group.repository.GroupMemberQueryRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final GroupRepository groupRepository;

    public List<TodoCategoryGetResponseDto> findAll(Long memberId, Long groupId ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException( NONE_USER ));

        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP ));

        Boolean hasTodoAuthority = groupMemberQueryRepository
                .existByMemberIdAndGroupIdAndTodoAuthority( member.getId(), group.getId() );

        if (!hasTodoAuthority) {
            throw new PlanusException( DO_NOT_HAVE_TODO_AUTHORITY );
        }

        List<GroupTodoCategory> groupTodoCategories = todoCategoryRepository.findGroupTodoCategoryAllByGroup( group );

        return groupTodoCategories.stream()
                .map( TodoCategoryGetResponseDto::of )
                .collect( Collectors.toList() );
    }

    @Transactional
    public TodoCategoryResponseDto createCategory(Long memberId, Long groupId, TodoCategoryRequestDto requestDto ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException( NONE_USER ));

        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP ));

        Boolean hasTodoAuthority = groupMemberQueryRepository
                .existByMemberIdAndGroupIdAndTodoAuthority( member.getId(), group.getId() );

        if (!hasTodoAuthority) {
            throw new PlanusException( DO_NOT_HAVE_TODO_AUTHORITY );
        }

        Color color = Color.of(requestDto.getColor());

        GroupTodoCategory groupTodoCategory = requestDto.toGroupTodoCategoryEntity( group, color );
        GroupTodoCategory saveGroupTodoCategory = todoCategoryRepository.save( groupTodoCategory );

        return TodoCategoryResponseDto.of( saveGroupTodoCategory );
    }

    @Transactional
    public TodoCategoryResponseDto changeCategory(Long memberId, Long groupId, Long categoryId, TodoCategoryRequestDto requestDto ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException( NONE_USER ));

        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP ));

        Boolean hasTodoAuthority = groupMemberQueryRepository
                .existByMemberIdAndGroupIdAndTodoAuthority( member.getId(), group.getId() );

        if (!hasTodoAuthority) {
            throw new PlanusException( DO_NOT_HAVE_TODO_AUTHORITY );
        }

        GroupTodoCategory groupTodoCategory = todoCategoryRepository.findGroupTodoCategoryByIdAndStatus( categoryId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_CATEGORY ));

        Color color = Color.of( requestDto.getColor() );
        groupTodoCategory.change( requestDto.getName(), color );

        return TodoCategoryResponseDto.of( groupTodoCategory );
    }

    @Transactional
    public TodoCategoryResponseDto deleteCategory(Long memberId, Long groupId, Long categoryId ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException( NONE_USER ));

        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP ));

        Boolean hasTodoAuthority = groupMemberQueryRepository
                .existByMemberIdAndGroupIdAndTodoAuthority( member.getId(), group.getId() );

        if (!hasTodoAuthority) {
            throw new PlanusException( DO_NOT_HAVE_TODO_AUTHORITY );
        }

        GroupTodoCategory groupTodoCategory = todoCategoryRepository.findGroupTodoCategoryByIdAndStatus( categoryId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_CATEGORY ));

        groupTodoCategory.changeStatusToInactive();

        return TodoCategoryResponseDto.of( groupTodoCategory );
    }

}
