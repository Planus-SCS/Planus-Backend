package scs.planus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scs.planus.auth.PrincipalDetails;
import scs.planus.common.response.BaseResponse;
import scs.planus.dto.todoCategory.CategoryRequestDto;
import scs.planus.dto.todoCategory.CategoryGetResponseDto;
import scs.planus.dto.todoCategory.CategoryResponseDto;
import scs.planus.service.CategoryService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public BaseResponse<List<CategoryGetResponseDto>> getAllCategory(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        List<CategoryGetResponseDto> responseDto = categoryService.findAll(principalDetails.getId());

        return new BaseResponse<>(responseDto);
    }

    @PostMapping("/categories")
    public BaseResponse<CategoryResponseDto> createCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @Valid @RequestBody CategoryRequestDto requestDto){
        Long memberId = principalDetails.getId();
        CategoryResponseDto responseDto = categoryService.createCategory( memberId,
                                                                            requestDto);

        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/categories/{categoryId}")
    public BaseResponse<CategoryResponseDto> changeCategory(@PathVariable(name = "categoryId") Long categoryId,
                                                            @RequestBody CategoryRequestDto requestDto) {

        CategoryResponseDto responseDto = categoryService.changeCategory( categoryId,
                                                                            requestDto);

        return new BaseResponse<>(responseDto);
    }

    @DeleteMapping("/categories/{categoryId}")
    public BaseResponse<CategoryResponseDto> deleteCategory(@PathVariable(name = "categoryId") Long categoryId) {

        CategoryResponseDto responseDto = categoryService.deleteCategory(categoryId);

        return new BaseResponse<>(responseDto);
    }
}
