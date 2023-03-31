package scs.planus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.common.exception.PlanusException;
import scs.planus.common.response.CustomResponseStatus;
import scs.planus.domain.Member;
import scs.planus.domain.TodoCategory;
import scs.planus.dto.todo.CategoryGetResponseDto;
import scs.planus.dto.todo.CategoryResponseDto;
import scs.planus.repository.CategoryRepository;
import scs.planus.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * 새로운 카테고리 생성
     */
    @Transactional
    public CategoryResponseDto createCategory(TodoCategory todoCategory) {
        TodoCategory saveCategory = categoryRepository.save(todoCategory);

        return new CategoryResponseDto(saveCategory);
    }

    /**
     * 카테고리 수정
     */
    @Transactional
    public CategoryResponseDto changeCategory(Long categoryId, TodoCategory category) {
        TodoCategory findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    throw new PlanusException(CustomResponseStatus.NOT_EXIST_CATEGORY);
                });

        findCategory.change(category.getName(), category.getColor());

        return new CategoryResponseDto(category);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
