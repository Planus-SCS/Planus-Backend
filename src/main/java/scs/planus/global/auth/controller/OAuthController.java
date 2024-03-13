package scs.planus.global.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.global.auth.dto.OAuthLoginResponseDto;
import scs.planus.global.auth.dto.apple.AppleAuthRequestDto;
import scs.planus.global.auth.dto.apple.AppleClientSecretResponseDto;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.auth.service.OAuthService;
import scs.planus.global.common.response.BaseResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OAuth", description = "OAuth API Document")
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/oauth/login/kakao")
    @Operation(summary = "Kakao OAuth API")
    public BaseResponse<OAuthLoginResponseDto> kakaoLogin(@RequestParam String code) {
        OAuthLoginResponseDto loginResponseDto = oAuthService.kakaoLogin(code);
        return new BaseResponse<>(loginResponseDto);
    }

    @GetMapping("/oauth/login/google")
    @Operation(summary = "Google OAuth API")
    public BaseResponse<OAuthLoginResponseDto> googleLogin(@RequestParam String code) {
        OAuthLoginResponseDto loginResponseDto = oAuthService.googleLogin(code);
        return new BaseResponse<>(loginResponseDto);
    }

    @PostMapping("/oauth/login/apple")
    @Operation(summary = "Apple OAuth API")
    public BaseResponse<OAuthLoginResponseDto> appleLogin(@Valid @RequestBody AppleAuthRequestDto appleAuthRequestDto) {
        OAuthLoginResponseDto loginResponseDto = oAuthService.appleLogin(appleAuthRequestDto);
        return new BaseResponse<>(loginResponseDto);
    }

    @GetMapping("/oauth/apple/client-secret")
    @Operation(summary = "Get Apple client_secret API")
    public BaseResponse<AppleClientSecretResponseDto> getAppleClientSecret(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        AppleClientSecretResponseDto appleClientSecretResponseDto = oAuthService.getClientSecret();
        return new BaseResponse<>(appleClientSecretResponseDto);
    }
}
