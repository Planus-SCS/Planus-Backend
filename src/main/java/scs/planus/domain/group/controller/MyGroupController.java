package scs.planus.domain.group.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scs.planus.domain.category.dto.TodoCategoryGetResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupDetailResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupGetMemberResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupOnlineStatusResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupResponseDto;
import scs.planus.domain.group.service.MyGroupService;
import scs.planus.domain.todo.dto.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MyGroup", description = "MyGroup API Document")
public class MyGroupController {

    private final MyGroupService myGroupService;

    @GetMapping("/my-groups")
    @Operation(summary = "전체 MyGroup 조회 API")
    public BaseResponse<List<MyGroupResponseDto>> getMyGroups(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getId();
        List<MyGroupResponseDto> responseDtos = myGroupService.getMyAllGroups(memberId);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("/my-groups/{groupId}")
    @Operation(summary = "개별 MyGroup 조회 API")
    public BaseResponse<MyGroupDetailResponseDto> getMyEachGroup(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                 @PathVariable Long groupId) {
        Long memberId = principalDetails.getId();
        MyGroupDetailResponseDto responseDto = myGroupService.getMyEachGroupDetail(memberId, groupId);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/my-groups/{groupId}/members")
    @Operation(summary = "MyGroup에 속한 그룹원 조회 API")
    public BaseResponse<List<MyGroupGetMemberResponseDto>> getGroupMembersForMember(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                                    @PathVariable("groupId") Long groupId ) {
        Long memberId = principalDetails.getId();
        List<MyGroupGetMemberResponseDto> responseDto = myGroupService.getGroupMembersForMember(memberId, groupId);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/my-groups/{groupId}/members/{memberId}/calendar")
    @Operation(summary = "MyGroup에 속한 그룹원 캘린더 조회 API")
    public BaseResponse<List<TodoDetailsResponseDto>> getGroupMemberPeriodTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                                @PathVariable Long groupId,
                                                                                @PathVariable Long memberId,
                                                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        Long loginId = principalDetails.getId();
        List<TodoDetailsResponseDto> responseDtos = myGroupService.getGroupMemberPeriodTodos(loginId, groupId, memberId, from, to);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("/my-groups/{groupId}/members/{memberId}/calendar/daily")
    @Operation(summary = "MyGroup에 속한 그룹원 일별 Todo/Schedule 조회 API")
    public BaseResponse<TodoDailyResponseDto> getGroupMemberPeriodTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                                @PathVariable Long groupId,
                                                                                @PathVariable Long memberId,
                                                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long loginId = principalDetails.getId();
        TodoDailyResponseDto responseDtos = myGroupService.getGroupMemberDailyTodos(loginId, groupId, memberId, date);
        return new BaseResponse<>(responseDtos);
    }

    @PatchMapping("/my-groups/{groupId}/online-status")
    @Operation(summary = "OnlineStatus 변경 API")
    public BaseResponse<MyGroupOnlineStatusResponseDto> updateOnlineStatus(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                           @PathVariable Long groupId) {
        Long memberId = principalDetails.getId();
        MyGroupOnlineStatusResponseDto responseDto = myGroupService.changeOnlineStatus(memberId, groupId);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/my-groups/{groupId}/members/{memberId}/categories")
    @Operation(summary = "Group의 대상 회원의 전체 Member Todo Category 조회 API")
    public BaseResponse<List<TodoCategoryGetResponseDto>> getAllTargetMemberTodoCategories(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                                           @PathVariable("groupId") Long groupId,
                                                                                           @PathVariable("memberId") Long memberId) {

        Long loginMemberId = principalDetails.getId();
        List<TodoCategoryGetResponseDto> responseDto = myGroupService.findAllTargetMemberTodoCategories( loginMemberId, groupId, memberId );

        return new BaseResponse<>(responseDto);
    }

}
