package scs.planus.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.exception.CustomExceptionStatus;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.category.dto.CategoryGetResponseDto;
import scs.planus.domain.category.dto.CategoryRequestDto;
import scs.planus.domain.category.dto.CategoryResponseDto;
import scs.planus.domain.category.repository.CategoryRepository;
import scs.planus.domain.member.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    /**
     * 카테고리 조회
     */
    public List<CategoryGetResponseDto> findAll(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    throw new PlanusException(CustomExceptionStatus.NONE_USER);
                });

        List<TodoCategory> todoCategories = categoryRepository.findAllByStatus(member);

        return todoCategories.stream()
                .map(CategoryGetResponseDto::of)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 카테고리 생성
     */
    @Transactional
    public CategoryResponseDto createCategory(Long memberId, CategoryRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    throw new PlanusException(CustomExceptionStatus.NONE_USER);
                });

        TodoCategory todoCategory = requestDto.toEntity(member);
        TodoCategory saveCategory = categoryRepository.save(todoCategory);

        return new CategoryResponseDto(saveCategory);
    }

    /**
     * 카테고리 수정
     */
    @Transactional
    public CategoryResponseDto changeCategory(Long categoryId, CategoryRequestDto requestDto) {
        TodoCategory findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    throw new PlanusException(CustomExceptionStatus.NOT_EXIST_CATEGORY);
                });

        Color color = Color.isValid(requestDto.getColor());
        findCategory.change(requestDto.getName(), color);

        return new CategoryResponseDto(findCategory);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public CategoryResponseDto deleteCategory(Long categoryId) {
        TodoCategory findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    throw new PlanusException(CustomExceptionStatus.NOT_EXIST_CATEGORY);
                });

        findCategory.changeStatusToInactive();

        return new CategoryResponseDto(findCategory);
    }

}
