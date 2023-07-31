package scs.planus.domain.group.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scs.planus.domain.group.dto.*;
import scs.planus.domain.group.service.GroupService;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.common.response.BaseResponse;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Group", description = "Group API Document")
public class GroupController {
    private final GroupService groupService;

    @GetMapping("/groups")
    @Operation(summary = "검색 홈에서 초기화면을 위한 Groups 조회 API")
    public BaseResponse<List<GroupsGetResponseDto>> getGroupsSearchHome(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                        Pageable pageable) {

        List<GroupsGetResponseDto> responseDtos = groupService.getGroupsSearchHome(pageable);

        return new BaseResponse<>(responseDtos);
    }

    @GetMapping("/groups/search")
    @Operation(summary = "그룹 이름 검색 API")
    public BaseResponse<List<GroupsGetResponseDto>> getGroupsSearch(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                    @RequestParam("keyword") String keyword,
                                                                    Pageable pageable) {

        List<GroupsGetResponseDto> responseDtos = groupService.getGroupsSearchByKeyword(keyword, pageable);

        return new BaseResponse<>(responseDtos);
    }

    @PostMapping("/groups")
    @Operation(summary = "그룹 생성 API")
    public BaseResponse<GroupResponseDto> createGroup(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @RequestPart(value = "image") MultipartFile multipartFile,
                                                      @Valid @RequestPart(value = "groupCreateRequestDto") GroupCreateRequestDto requestDto) {
        Long memberId = principalDetails.getId();
        GroupResponseDto responseDto = groupService.createGroup(memberId, requestDto, multipartFile);

        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/groups/{groupId}")
    @Operation(summary = "그룹 상세 정보 조회 API")
    public BaseResponse<GroupGetDetailResponseDto> getGroupDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                  @PathVariable("groupId") Long groupId) {

        Long memberId = principalDetails.getId();
        GroupGetDetailResponseDto responseDto = groupService.getGroupDetail(memberId, groupId);

        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/groups/{groupId}/members")
    @Operation(summary = "그룹 회원 정보 조회 API")
    public BaseResponse<List<GroupGetMemberResponseDto>> getGroupMember(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                        @PathVariable("groupId") Long groupId) {

        List<GroupGetMemberResponseDto> responseDto = groupService.getGroupMember(groupId);

        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/groups/{groupId}")
    @Operation(summary = "(리더용) 그룹 상세 정보 수정 API")
    public BaseResponse<GroupResponseDto> updateGroupDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @PathVariable("groupId") Long groupId,
                                                            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
                                                            @Valid @RequestPart(value = "groupUpdateRequestDto") GroupDetailUpdateRequestDto requestDto  ) {
        Long memberId = principalDetails.getId();
        GroupResponseDto responseDto = groupService.updateGroupDetail( memberId, groupId, requestDto, multipartFile );

        return new BaseResponse<>( responseDto );
    }

    @PatchMapping("/groups/{groupId}/notice")
    @Operation(summary = "(리더용) 그룹 공지 수정 API")
    public BaseResponse<GroupResponseDto> updateGroupNotice(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @PathVariable("groupId") Long groupId,
                                                            @Valid @RequestBody GroupNoticeUpdateRequestDto requestDto ) {

        Long memberId = principalDetails.getId();
        GroupResponseDto responseDto = groupService.updateGroupNotice( memberId, groupId, requestDto );

        return new BaseResponse<>( responseDto );
    }

    @DeleteMapping("/groups/{groupId}")
    @Operation(summary = "(리더용) 그룹 삭제 API")
    public BaseResponse<GroupResponseDto> softDeleteGroup(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                          @PathVariable("groupId") Long groupId ){

        Long memberId = principalDetails.getId();
        GroupResponseDto responseDto = groupService.softDeleteGroup( memberId, groupId );

        return new BaseResponse<>( responseDto );
    }

    @DeleteMapping("/groups/{groupId}/members/{memberId}")
    @Operation(summary = "(리더용) 그룹 회원 강제 탈퇴 API")
    public BaseResponse<GroupMemberResponseDto> withdrawGroupMember(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                    @PathVariable("groupId") Long groupId,
                                                                    @PathVariable("memberId") Long memberId) {
        Long leaderId = principalDetails.getId();
        GroupMemberResponseDto responseDto = groupService.withdrawGroupMember(leaderId, memberId, groupId);

        return new BaseResponse<>(responseDto);
    }

    @DeleteMapping("/groups/{groupId}/withdraw")
    @Operation(summary = "(회원용) 그룹 자발적 탈퇴 API")
    public BaseResponse<GroupMemberResponseDto> withdraw(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @PathVariable("groupId") Long groupId) {
        Long memberId = principalDetails.getId();
        GroupMemberResponseDto responseDto = groupService.softWithdraw(memberId, groupId);

        return new BaseResponse<>(responseDto);
    }

}
