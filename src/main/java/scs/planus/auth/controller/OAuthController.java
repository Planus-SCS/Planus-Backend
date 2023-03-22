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
import scs.planus.common.exception.PlanusException;
import scs.planus.common.response.BaseResponse;
import scs.planus.common.response.CustomResponseStatus;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final KakakoService kakakoService;
    private final GoogleService googleService;

    @GetMapping("/oauth2/{socialType}")
    public BaseResponse<OAuthLoginResponseDto> socialLogin(@PathVariable String socialType,
                                                           @RequestParam String code) {
        switch (socialType) {
            case "google":
                return new BaseResponse<>(googleService.login(socialType, code));
            case "kakao":
                return new BaseResponse<>(kakakoService.login(socialType, code));
        }
        throw new PlanusException(CustomResponseStatus.NONE_SOCIAL_TYPE);
    }
}
