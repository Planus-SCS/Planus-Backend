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
@Tag(name = "Member Todo Category", description = "Member Todo Category API Document")
public class MemberTodoCategoryController {
    private final MemberTodoCategoryService memberTodoCategoryService;

    @GetMapping("/categories")
    @Operation(summary = "전체 Member Todo  Category 조회 API")
    public BaseResponse<List<TodoCategoryGetResponseDto>> getAllMemberTodoCategory(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getId();
        List<TodoCategoryGetResponseDto> responseDto = memberTodoCategoryService.findAll(memberId);

        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/categories/groups")
    @Operation(summary = "속한 Group Todo Category 조회 API")
    public BaseResponse<List<TodoCategoryGetResponseDto>> getAllGroupTodoCategory(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getId();
        List<TodoCategoryGetResponseDto> responseDto = memberTodoCategoryService.findAllGroupTodoCategories(memberId);

        return new BaseResponse<>(responseDto);
    }

    @PostMapping("/categories")
    @Operation(summary = "Member Todo Category 생성 API")
    public BaseResponse<TodoCategoryResponseDto> createMemberTodoCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                          @Valid @RequestBody TodoCategoryRequestDto requestDto){
        Long memberId = principalDetails.getId();
        TodoCategoryResponseDto responseDto = memberTodoCategoryService.createCategory( memberId,
                                                                            requestDto);

        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/categories/{categoryId}")
    @Operation(summary = "Member Todo Category 변경 API")
    public BaseResponse<TodoCategoryResponseDto> modifyMemberTodoCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                          @PathVariable(name = "categoryId") Long categoryId,
                                                                          @Valid @RequestBody TodoCategoryRequestDto requestDto) {

        TodoCategoryResponseDto responseDto = memberTodoCategoryService.changeCategory( categoryId,
                                                                            requestDto);

        return new BaseResponse<>(responseDto);
    }

    @DeleteMapping("/categories/{categoryId}")
    @Operation(summary = "Member Todo Category 삭제 API")
    public BaseResponse<TodoCategoryResponseDto> deleteMemberTodoCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                          @PathVariable(name = "categoryId") Long categoryId) {

        TodoCategoryResponseDto responseDto = memberTodoCategoryService.deleteCategory(categoryId);

        return new BaseResponse<>(responseDto);
    }
}
