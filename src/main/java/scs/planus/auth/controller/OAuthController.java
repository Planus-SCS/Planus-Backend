package scs.planus.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.auth.dto.OAuthLoginResponseDto;
import scs.planus.auth.service.GoogleService;
import scs.planus.auth.service.KakakoService;
import scs.planus.auth.service.OAuthService;
import scs.planus.common.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final KakakoService kakakoService;
    private final GoogleService googleService;

    @GetMapping("/oauth2/{socialType}")
    public BaseResponse socialLogin(@PathVariable String socialType,
                                    @RequestParam String code) {
        OAuthLoginResponseDto loginResponseDto = oAuthService.login(socialType, code);
        return new BaseResponse(loginResponseDto);
    }
}
