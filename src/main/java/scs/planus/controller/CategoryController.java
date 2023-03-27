package scs.planus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import scs.planus.common.response.BaseResponse;
import scs.planus.dto.todo.CategoryChangeRequestDto;
import scs.planus.dto.todo.CategoryCreateRequestDto;
import scs.planus.dto.todo.CategoryResponseDto;
import scs.planus.service.MemberService;
import scs.planus.service.CategoryService;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class CategoryController {
    private final MemberService memberService;
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public void getAllCategory() {

    }

    @PostMapping("/categories")
    public BaseResponse<CategoryResponseDto> createCategory(@RequestBody CategoryCreateRequestDto requestDto){
        CategoryResponseDto responseDto = categoryService.createCategory(requestDto.toEntity());

        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/categories/{categoryId}")
    public BaseResponse<CategoryResponseDto> getCategory(@PathVariable(name = "categoryId") Long categoryId) {
        CategoryResponseDto responseDto = categoryService.findCategoryById(categoryId);

        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/categories/{categoryId}")
    public BaseResponse<CategoryResponseDto> changeCategory(@PathVariable(name = "categoryId") Long categoryId,
                                                            @RequestBody CategoryChangeRequestDto requestDto) {
        CategoryResponseDto responseDto = categoryService.changeCategory(categoryId, requestDto.toEntity());

        return new BaseResponse<>(responseDto);
    }

    @DeleteMapping("/categories/{categoryId}")
    public BaseResponse<String> deleteCategory(@PathVariable(name = "categoryId") Long categoryId) {
        categoryService.deleteTodo(categoryId);

        return new BaseResponse<>("삭제 완료");
    }
}
