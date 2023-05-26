package scs.planus.domain.group.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scs.planus.domain.group.dto.groupJoin.GroupJoinGetResponseDto;
import scs.planus.domain.group.dto.groupJoin.GroupJoinResponseDto;
import scs.planus.domain.group.dto.GroupMemberResponseDto;
import scs.planus.domain.group.service.GroupJoinService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Group Join", description = "Group Join API Document")
public class GroupJoinController {

    private final GroupJoinService groupJoinService;

    @PostMapping("/group-joins/groups/{groupId}")
    @Operation(summary = "그룹 가입 요청 API")
    public BaseResponse<GroupJoinResponseDto> joinGroup(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                        @PathVariable("groupId") Long groupId ) {
        Long memberId = principalDetails.getId();
        GroupJoinResponseDto responseDto = groupJoinService.joinGroup( memberId, groupId );

        return new BaseResponse<>( responseDto );
    }

    @GetMapping("/group-joins")
    @Operation(summary = "(리더용) 그룹의 모든 가입 요청 조회 API")
    public BaseResponse<List<GroupJoinGetResponseDto>> getAllGroupJoin(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getId();
        List<GroupJoinGetResponseDto> responseDto = groupJoinService.getAllGroupJoin( memberId );

        return new BaseResponse<>( responseDto );
    }

    @PostMapping("/group-joins/{groupJoinId}/accept")
    @Operation(summary = "(리더용) 그룹 가입 요청 수락 API")
    public BaseResponse<GroupMemberResponseDto> acceptGroupJoin(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                @PathVariable("groupJoinId") Long groupJoinId ){

        Long memberId = principalDetails.getId();
        GroupMemberResponseDto responseDto = groupJoinService.acceptGroupJoin( memberId, groupJoinId );

        return new BaseResponse<>( responseDto );
    }

    @PostMapping("/group-joins/{groupJoinId}/reject")
    @Operation(summary = "(리더용) 그룹 가입 요청 거절 API")
    public BaseResponse<GroupJoinResponseDto> rejectGroupJoin(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                              @PathVariable("groupJoinId") Long groupJoinId ){

        Long memberId = principalDetails.getId();
        GroupJoinResponseDto responseDto = groupJoinService.rejectGroupJoin( memberId, groupJoinId );

        return new BaseResponse<>( responseDto );
    }
}
