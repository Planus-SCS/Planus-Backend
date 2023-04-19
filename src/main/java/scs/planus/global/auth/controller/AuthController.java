package scs.planus.global.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scs.planus.global.auth.dto.TokenReissueRequestDto;
import scs.planus.global.auth.dto.TokenReissueResponseDto;
import scs.planus.global.auth.service.AuthService;
import scs.planus.global.common.response.BaseResponse;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/token-reissue")
    public BaseResponse<TokenReissueResponseDto> tokenReissue(@RequestBody TokenReissueRequestDto requestDto) {
        TokenReissueResponseDto responseDto = authService.reissue(requestDto);
        return new BaseResponse<>(responseDto);
    }
}
