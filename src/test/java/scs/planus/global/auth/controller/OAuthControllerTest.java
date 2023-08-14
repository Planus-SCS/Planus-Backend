package scs.planus.global.auth.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import scs.planus.global.auth.dto.OAuthLoginResponseDto;
import scs.planus.global.auth.dto.apple.AppleAuthRequestDto;
import scs.planus.global.auth.dto.apple.FullName;
import scs.planus.global.auth.service.apple.AppleOAuthService;
import scs.planus.global.auth.service.OAuthService;
import scs.planus.support.ControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OAuthController.class)
class OAuthControllerTest extends ControllerTest {

    @MockBean
    private OAuthService oAuthService;
    @MockBean
    private AppleOAuthService appleOAuthService;

    @DisplayName("소셜로그인이 정상적으로 작동되어야 한다.")
    @Test
    void socialLogin() throws Exception {
        //given
        String path = "/app/oauth/login/{provider}";
        String provider = "kakao";
        String code = "authorizationCode";

        given(oAuthService.login(anyString(), anyString()))
                .willReturn(OAuthLoginResponseDto.builder()
                        .memberId(1L)
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .build());

        //when & then
        mockMvc
                .perform(get(path, provider)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("code", code))
                .andDo(print())
                .andExpect(status().isOk());
    }
}