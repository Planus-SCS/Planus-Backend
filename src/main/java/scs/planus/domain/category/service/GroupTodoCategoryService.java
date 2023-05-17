package scs.planus.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.category.dto.CategoryGetResponseDto;
import scs.planus.domain.category.dto.CategoryRequestDto;
import scs.planus.domain.category.dto.CategoryResponseDto;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final GroupRepository groupRepository;

    public List<CategoryGetResponseDto> findAll( Long memberId, Long groupId ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException( NONE_USER ));

        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP ));

        Boolean isTodoAuthority = groupMemberQueryRepository
                .existByMemberIdAndGroupIdAndTodoAuthority( member.getId(), group.getId() );

        if (!isTodoAuthority) {
            throw new PlanusException( DO_NOT_HAVE_TODO_AUTHORITY );
        }

        List<GroupTodoCategory> groupTodoCategories = categoryRepository.findAllByGroup( group );

        return groupTodoCategories.stream()
                .map( CategoryGetResponseDto::of )
                .collect( Collectors.toList() );
    }

    @Transactional
    public CategoryResponseDto createCategory( Long memberId, Long groupId, CategoryRequestDto requestDto ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException( NONE_USER ));

        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP ));

        Boolean isTodoAuthority = groupMemberQueryRepository
                .existByMemberIdAndGroupIdAndTodoAuthority( member.getId(), group.getId() );

        if (!isTodoAuthority) {
            throw new PlanusException( DO_NOT_HAVE_TODO_AUTHORITY );
        }

        Color color = Color.translate(requestDto.getColor());

        GroupTodoCategory groupTodoCategory = requestDto.toGroupTodoCategoryEntity( group, color );
        GroupTodoCategory saveGroupTodoCategory = categoryRepository.save( groupTodoCategory );

        return CategoryResponseDto.of( saveGroupTodoCategory );
    }

    @Transactional
    public CategoryResponseDto changeCategory( Long memberId, Long groupId, Long categoryId, CategoryRequestDto requestDto ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException( NONE_USER ));

        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP ));

        Boolean isTodoAuthority = groupMemberQueryRepository
                .existByMemberIdAndGroupIdAndTodoAuthority( member.getId(), group.getId() );

        if (!isTodoAuthority) {
            throw new PlanusException( DO_NOT_HAVE_TODO_AUTHORITY );
        }

        GroupTodoCategory groupTodoCategory = categoryRepository.findByIdAndStatus( categoryId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_CATEGORY ));

        Color color = Color.translate( requestDto.getColor() );
        groupTodoCategory.change( requestDto.getName(), color );

        return CategoryResponseDto.of( groupTodoCategory );
    }

    @Transactional
    public CategoryResponseDto deleteCategory( Long memberId, Long groupId,Long categoryId ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException( NONE_USER ));

        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP ));

        Boolean isTodoAuthority = groupMemberQueryRepository
                .existByMemberIdAndGroupIdAndTodoAuthority( member.getId(), group.getId() );

        if (!isTodoAuthority) {
            throw new PlanusException( DO_NOT_HAVE_TODO_AUTHORITY );
        }

        GroupTodoCategory groupTodoCategory = categoryRepository.findByIdAndStatus( categoryId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_CATEGORY ));

        groupTodoCategory.changeStatusToInactive();

        return CategoryResponseDto.of( groupTodoCategory );
    }

}
