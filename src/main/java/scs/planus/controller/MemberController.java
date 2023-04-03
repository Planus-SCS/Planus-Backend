package scs.planus.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import scs.planus.auth.PrincipalDetails;
import scs.planus.common.response.BaseResponse;
import scs.planus.dto.member.MemberResponseDto;
import scs.planus.dto.member.MemberUpdateRequestDto;
import scs.planus.service.MemberService;

import javax.validation.Valid;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members")
    public BaseResponse<MemberResponseDto> showDetail(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getId();
        MemberResponseDto responseDto = memberService.getDetail(memberId);
        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/members")
    public BaseResponse<MemberResponseDto> updateProfile(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @RequestPart(value = "image", required = false) MultipartFile multipartFile,
                                                         @Valid @RequestPart(value = "updateRequestDto") MemberUpdateRequestDto memberUpdateRequestDto) {
        Long memberId = principalDetails.getId();
        MemberResponseDto responseDto = memberService.update(memberId, multipartFile, memberUpdateRequestDto);
        return new BaseResponse<>(responseDto);
    }
}
