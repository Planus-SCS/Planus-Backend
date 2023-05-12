package scs.planus.domain.group.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
public class MyGroupController {

    private final MyGroupService myGroupService;

    @GetMapping("/my-groups")
    public BaseResponse<List<MyGroupResponseDto>> getMyGroups(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getId();
        List<MyGroupResponseDto> responseDtos = myGroupService.getMyAllGroups(memberId);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("/my-groups/{groupId}")
    public BaseResponse<MyGroupDetailResponseDto> getMyEachGroup(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                 @PathVariable Long groupId) {
        Long memberId = principalDetails.getId();
        MyGroupDetailResponseDto responseDto = myGroupService.getMyEachGroupDetail(memberId, groupId);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/my-groups/{groupId}/members")
    public BaseResponse<List<MyGroupGetMemberResponseDto>> getGroupMembersForMember(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                                    @PathVariable("groupId") Long groupId ) {
        Long memberId = principalDetails.getId();
        List<MyGroupGetMemberResponseDto> responseDto = myGroupService.getGroupMembersForMember(memberId, groupId);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/my-groups/{groupId}/members/{memberId}/calendar")
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
    public BaseResponse<TodoDailyResponseDto> getGroupMemberPeriodTodos(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                                @PathVariable Long groupId,
                                                                                @PathVariable Long memberId,
                                                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long loginId = principalDetails.getId();
        TodoDailyResponseDto responseDtos = myGroupService.getGroupMemberDailyTodos(loginId, groupId, memberId, date);
        return new BaseResponse<>(responseDtos);
    }

    @PatchMapping("/my-groups/{groupId}/online-status")
    public BaseResponse<MyGroupOnlineStatusResponseDto> updateOnlineStatus(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                           @PathVariable Long groupId) {
        Long memberId = principalDetails.getId();
        MyGroupOnlineStatusResponseDto responseDto = myGroupService.changeOnlineStatus(memberId, groupId);
        return new BaseResponse<>(responseDto);
    }
}
