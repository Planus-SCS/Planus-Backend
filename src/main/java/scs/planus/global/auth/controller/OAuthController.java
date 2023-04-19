package scs.planus.global.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.global.auth.dto.OAuthLoginResponseDto;
import scs.planus.global.auth.service.OAuthService;
import scs.planus.global.common.response.BaseResponse;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/oauth/{provider}")
    public BaseResponse<OAuthLoginResponseDto> socialLogin(@PathVariable String provider,
                                                           @RequestParam String code) {

        OAuthLoginResponseDto loginResponseDto = oAuthService.login(provider, code);
        return new BaseResponse<>(loginResponseDto);
    }
}
