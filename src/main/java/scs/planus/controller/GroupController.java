package scs.planus.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scs.planus.auth.PrincipalDetails;
import scs.planus.common.response.BaseResponse;
import scs.planus.dto.group.GroupCreateRequestDto;
import scs.planus.dto.group.GroupGetResponseDto;
import scs.planus.dto.group.GroupResponseDto;
import scs.planus.service.GroupService;

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
    public BaseResponse<GroupGetResponseDto> getGroup( @AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @PathVariable("groupId") Long groupId ) {

        GroupGetResponseDto responseDto = groupService.getGroup( groupId );

        return new BaseResponse<>( responseDto );
    }
}
