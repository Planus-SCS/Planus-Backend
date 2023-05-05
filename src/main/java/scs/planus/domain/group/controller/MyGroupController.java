package scs.planus.domain.group.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.domain.group.dto.mygroup.MyGroupOnlineStatusResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupResponseDto;
import scs.planus.domain.group.service.MyGroupService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

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
        List<MyGroupResponseDto> responseDtos = myGroupService.getMyGroups(memberId);
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
