package scs.planus.domain.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scs.planus.domain.category.dto.TodoCategoryGetResponseDto;
import scs.planus.domain.category.dto.TodoCategoryRequestDto;
import scs.planus.domain.category.dto.TodoCategoryResponseDto;
import scs.planus.domain.category.service.GroupTodoCategoryService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Tag(name = "Group Todo Category", description = "Group Todo Category API Document")
public class GroupTodoCategoryController {
    private final GroupTodoCategoryService groupTodoCategoryService;

    @GetMapping("/my-groups/{groupId}/categories")
    @Operation(summary = "전체 Group Todo Category 조회 API")
    public BaseResponse<List<TodoCategoryGetResponseDto>> getAllGroupTodoCategories(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                                    @PathVariable("groupId") Long groupId) {

        Long memberId = principalDetails.getId();
        List<TodoCategoryGetResponseDto> responseDto = groupTodoCategoryService.findAll( memberId, groupId );

        return new BaseResponse<>(responseDto);
    }

    @PostMapping("/my-groups/{groupId}/categories")
    @Operation(summary = "Group Todo Category 생성 API")
    public BaseResponse<TodoCategoryResponseDto> createGroupTodoCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                         @PathVariable("groupId") Long groupId,
                                                                         @Valid @RequestBody TodoCategoryRequestDto requestDto){
        Long memberId = principalDetails.getId();
        TodoCategoryResponseDto responseDto = groupTodoCategoryService.createCategory( memberId, groupId, requestDto );

        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/my-groups/{groupId}/categories/{categoryId}")
    @Operation(summary = "Group Todo Category 변경 API")
    public BaseResponse<TodoCategoryResponseDto> modifyGroupTodoCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                         @PathVariable("groupId") Long groupId,
                                                                         @PathVariable("categoryId") Long categoryId,
                                                                         @Valid @RequestBody TodoCategoryRequestDto requestDto) {
        Long memberId = principalDetails.getId();
        TodoCategoryResponseDto responseDto = groupTodoCategoryService.changeCategory( memberId, groupId, categoryId, requestDto);

        return new BaseResponse<>(responseDto);
    }

    @DeleteMapping("/my-groups/{groupId}/categories/{categoryId}")
    @Operation(summary = "Group Todo Category 삭제(soft) API")
    public BaseResponse<TodoCategoryResponseDto> deleteGroupTodoCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                         @PathVariable("groupId") Long groupId,
                                                                         @PathVariable("categoryId") Long categoryId) {
        Long memberId = principalDetails.getId();
        TodoCategoryResponseDto responseDto = groupTodoCategoryService.deleteCategory( memberId, groupId, categoryId );

        return new BaseResponse<>(responseDto);
    }
}
