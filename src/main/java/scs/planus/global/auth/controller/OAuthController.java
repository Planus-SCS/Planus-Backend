package scs.planus.global.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scs.planus.global.auth.dto.OAuthLoginResponseDto;
import scs.planus.global.auth.dto.apple.AppleAuthRequestDto;
import scs.planus.global.auth.dto.apple.AppleClientSecretResponseDto;
import scs.planus.global.auth.entity.PrincipalDetails;
import scs.planus.global.auth.service.OAuthService;
import scs.planus.global.auth.service.apple.AppleOAuthService;
import scs.planus.global.common.response.BaseResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OAuth", description = "OAuth API Document")
public class OAuthController {

    private final OAuthService oAuthService;
    private final AppleOAuthService appleOAuthService;

    @GetMapping("/oauth/login/{provider}")
    @Operation(summary = "OAuth API")
    public BaseResponse<OAuthLoginResponseDto> socialLogin(@PathVariable String provider,
                                                           @RequestParam String code) {
        OAuthLoginResponseDto loginResponseDto = oAuthService.login(provider, code);
        return new BaseResponse<>(loginResponseDto);
    }

    @PostMapping("/oauth/login/apple")
    @Operation(summary = "Apple OAuth API")
    public BaseResponse<OAuthLoginResponseDto> appleLogin(@Valid @RequestBody AppleAuthRequestDto appleAuthRequestDto) {

        OAuthLoginResponseDto loginResponseDto = appleOAuthService.login(appleAuthRequestDto);
        return new BaseResponse<>(loginResponseDto);
    }

    @GetMapping("/oauth/logout/apple/client-secret")
    @Operation(summary = "Get Apple client_secret API")
    public BaseResponse<AppleClientSecretResponseDto> getAppleClientSecret(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        AppleClientSecretResponseDto appleClientSecretResponseDto = appleOAuthService.getClientSecret();
        return new BaseResponse<>(appleClientSecretResponseDto);
    }
}
