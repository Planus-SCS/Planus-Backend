package scs.planus.domain.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;
import scs.planus.domain.category.dto.TodoCategoryRequestDto;
import scs.planus.domain.category.dto.TodoCategoryGetResponseDto;
import scs.planus.domain.category.dto.TodoCategoryResponseDto;
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
    public BaseResponse<List<TodoCategoryGetResponseDto>> getAllCategory(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getId();
        List<TodoCategoryGetResponseDto> responseDto = memberTodoCategoryService.findAll(memberId);

        return new BaseResponse<>(responseDto);
    }

    @PostMapping("/categories")
    @Operation(summary = "Category 생성 API")
    public BaseResponse<TodoCategoryResponseDto> createCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                @Valid @RequestBody TodoCategoryRequestDto requestDto){
        Long memberId = principalDetails.getId();
        TodoCategoryResponseDto responseDto = memberTodoCategoryService.createCategory( memberId,
                                                                            requestDto);

        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/categories/{categoryId}")
    @Operation(summary = "Category 변경 API")
    public BaseResponse<TodoCategoryResponseDto> changeCategory(@PathVariable(name = "categoryId") Long categoryId,
                                                                @RequestBody TodoCategoryRequestDto requestDto) {

        TodoCategoryResponseDto responseDto = memberTodoCategoryService.changeCategory( categoryId,
                                                                            requestDto);

        return new BaseResponse<>(responseDto);
    }

    @DeleteMapping("/categories/{categoryId}")
    @Operation(summary = "Category 삭제 API")
    public BaseResponse<TodoCategoryResponseDto> deleteCategory(@PathVariable(name = "categoryId") Long categoryId) {

        TodoCategoryResponseDto responseDto = memberTodoCategoryService.deleteCategory(categoryId);

        return new BaseResponse<>(responseDto);
    }
}
