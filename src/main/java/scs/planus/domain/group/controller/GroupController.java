package scs.planus.domain.group.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scs.planus.domain.group.dto.*;
import scs.planus.domain.group.service.GroupService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/groups")
    public BaseResponse<GroupResponseDto> createGroup(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @RequestPart(value = "image") MultipartFile multipartFile,
                                                         @Valid @RequestPart(value = "groupCreateRequestDto") GroupCreateRequestDto requestDto ) {
        Long memberId = principalDetails.getId();
        GroupResponseDto responseDto = groupService.createGroup( memberId, requestDto, multipartFile );

        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/groups/{groupId}")
    public BaseResponse<GroupGetResponseDto> getGroupForNonMember( @AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @PathVariable("groupId") Long groupId ) {

        GroupGetResponseDto responseDto = groupService.getGroupForNonMember( groupId );

        return new BaseResponse<>( responseDto );
    }

    @PatchMapping("/groups/{groupId}")
    public BaseResponse<GroupResponseDto> updateGroupDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @PathVariable("groupId") Long groupId,
                                                            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
                                                            @Valid @RequestPart(value = "groupUpdateRequestDto", required = false) GroupDetailUpdateRequestDto requestDto  ) {
        Long memberId = principalDetails.getId();
        GroupResponseDto responseDto = groupService.updateGroupDetail( memberId, groupId, requestDto, multipartFile );

        return new BaseResponse<>( responseDto );
    }

    @PatchMapping("/groups/{groupId}/notice")
    public BaseResponse<GroupResponseDto> updateGroupNotice(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @PathVariable("groupId") Long groupId,
                                                            @Valid @RequestBody GroupNoticeUpdateRequestDto requestDto ) {

        Long memberId = principalDetails.getId();
        GroupResponseDto responseDto = groupService.updateGroupNotice( memberId, groupId, requestDto );

        return new BaseResponse<>( responseDto );
    }

    @DeleteMapping("/groups/{groupId}")
    public BaseResponse<GroupResponseDto> softDeleteGroup(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                          @PathVariable("groupId") Long groupId ){

        Long memberId = principalDetails.getId();
        GroupResponseDto responseDto = groupService.softDeleteGroup( memberId, groupId );

        return new BaseResponse<>( responseDto );
    }

    @PostMapping("/groups/{groupId}")
    public BaseResponse<GroupResponseDto> joinGroup(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable("groupId") Long groupId ) {
        Long memberId = principalDetails.getId();
        GroupResponseDto responseDto = groupService.joinGroup( groupId, memberId );

        return new BaseResponse<>(responseDto);
    }
}
