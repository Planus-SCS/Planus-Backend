package scs.planus.domain.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;
import scs.planus.domain.category.dto.CategoryRequestDto;
import scs.planus.domain.category.dto.CategoryGetResponseDto;
import scs.planus.domain.category.dto.CategoryResponseDto;
import scs.planus.domain.category.service.MemberTodoCategoryService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category API Document")
public class MemberTodoCategoryController {
    private final MemberTodoCategoryService memberTodoCategoryService;

    @GetMapping("/categories")
    @Operation(summary = "전체 Category 조회 API")
    public BaseResponse<List<CategoryGetResponseDto>> getAllCategory(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getId();
        List<CategoryGetResponseDto> responseDto = memberTodoCategoryService.findAll(memberId);

        return new BaseResponse<>(responseDto);
    }

    @PostMapping("/categories")
    @Operation(summary = "Category 생성 API")
    public BaseResponse<CategoryResponseDto> createCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @Valid @RequestBody CategoryRequestDto requestDto){
        Long memberId = principalDetails.getId();
        CategoryResponseDto responseDto = memberTodoCategoryService.createCategory( memberId,
                                                                            requestDto);

        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/categories/{categoryId}")
    @Operation(summary = "Category 변경 API")
    public BaseResponse<CategoryResponseDto> changeCategory(@PathVariable(name = "categoryId") Long categoryId,
                                                            @RequestBody CategoryRequestDto requestDto) {

        CategoryResponseDto responseDto = memberTodoCategoryService.changeCategory( categoryId,
                                                                            requestDto);

        return new BaseResponse<>(responseDto);
    }

    @DeleteMapping("/categories/{categoryId}")
    @Operation(summary = "Category 삭제 API")
    public BaseResponse<CategoryResponseDto> deleteCategory(@PathVariable(name = "categoryId") Long categoryId) {

        CategoryResponseDto responseDto = memberTodoCategoryService.deleteCategory(categoryId);

        return new BaseResponse<>(responseDto);
    }
}
