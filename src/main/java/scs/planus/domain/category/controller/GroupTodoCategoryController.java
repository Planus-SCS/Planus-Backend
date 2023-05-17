package scs.planus.domain.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scs.planus.domain.category.dto.CategoryGetResponseDto;
import scs.planus.domain.category.dto.CategoryRequestDto;
import scs.planus.domain.category.dto.CategoryResponseDto;
import scs.planus.domain.category.service.GroupTodoCategoryService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Tag(name = "Group Category", description = "Group Category API Document")
public class GroupTodoCategoryController {
    private final GroupTodoCategoryService groupTodoCategoryService;

    @GetMapping("/my-groups/{groupId}/categories")
    @Operation(summary = "전체 Group Category 조회 API")
    public BaseResponse<List<CategoryGetResponseDto>> getAllGroupCategories(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                            @PathVariable("groupId") Long groupId) {

        Long memberId = principalDetails.getId();
        List<CategoryGetResponseDto> responseDto = groupTodoCategoryService.findAll( memberId, groupId );

        return new BaseResponse<>(responseDto);
    }

    @PostMapping("/my-groups/{groupId}/categories")
    @Operation(summary = "Group Category 생성 API")
    public BaseResponse<CategoryResponseDto> createGroupCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                 @PathVariable("groupId") Long groupId,
                                                                 @Valid @RequestBody CategoryRequestDto requestDto){
        Long memberId = principalDetails.getId();
        CategoryResponseDto responseDto = groupTodoCategoryService.createCategory( memberId, groupId, requestDto );

        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/my-groups/{groupId}/categories/{categoryId}")
    @Operation(summary = "Group Category 변경 API")
    public BaseResponse<CategoryResponseDto> modifyGroupCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                 @PathVariable("groupId") Long groupId,
                                                                 @PathVariable("categoryId") Long categoryId,
                                                                 @RequestBody CategoryRequestDto requestDto) {
        Long memberId = principalDetails.getId();
        CategoryResponseDto responseDto = groupTodoCategoryService.changeCategory( memberId, groupId, categoryId, requestDto);

        return new BaseResponse<>(responseDto);
    }

    @DeleteMapping("/my-groups/{groupId}/categories/{categoryId}")
    @Operation(summary = "Group Category 삭제(soft) API")
    public BaseResponse<CategoryResponseDto> deleteGroupCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                 @PathVariable("groupId") Long groupId,
                                                                 @PathVariable("categoryId") Long categoryId) {
        Long memberId = principalDetails.getId();
        CategoryResponseDto responseDto = groupTodoCategoryService.deleteCategory( memberId, groupId, categoryId );

        return new BaseResponse<>(responseDto);
    }
}
