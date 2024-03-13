package scs.planus.global.auth.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import scs.planus.global.auth.dto.OAuthLoginResponseDto;
import scs.planus.global.auth.dto.apple.AppleAuthRequestDto;
import scs.planus.global.auth.dto.apple.AppleClientSecretResponseDto;
import scs.planus.global.auth.dto.apple.FullName;
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

    @DisplayName("소셜로그인이 정상적으로 작동되어야 한다.")
    @Test
    void socialLogin() throws Exception {
        //given
        String path = "/app/oauth/login/kakao";
        String code = "authorizationCode";

        given(oAuthService.kakaoLogin(anyString()))
                .willReturn(OAuthLoginResponseDto.builder()
                        .memberId(1L)
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .build());

        //when & then
        mockMvc
                .perform(get(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("code", code))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Apple 로그인이 정상적으로 작동되어야 한다.")
    @Test
    void appleLogin_Success() throws Exception {
        // given
        String path = "/app/oauth/login/apple";

        FullName fullName = FullName.builder()
                .givenName("창재")
                .familyName("이")
                .build();

        AppleAuthRequestDto appleAuthRequestDto = AppleAuthRequestDto.builder()
                .identityToken("identityToken")
                .fullName(fullName)
                .build();

        given(oAuthService.appleLogin(any(AppleAuthRequestDto.class)))
                .willReturn(OAuthLoginResponseDto.builder()
                        .memberId(1L)
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .build());

        //when & then
        mockMvc
                .perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appleAuthRequestDto))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Apple 로그인시, identityToken 값이 비어 있으면 예외를 발생시킨다.")
    @Test
    void appleLogin_Fail() throws Exception {
        // given
        String path = "/app/oauth/login/apple";

        FullName fullName = FullName.builder()
                .givenName("창재")
                .familyName("이")
                .build();

        AppleAuthRequestDto appleAuthRequestDto = AppleAuthRequestDto.builder()
                .fullName(fullName)
                .build();

        given(oAuthService.appleLogin(any(AppleAuthRequestDto.class)))
                .willReturn(OAuthLoginResponseDto.builder()
                        .memberId(1L)
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .build());

        //when & then
        mockMvc
                .perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appleAuthRequestDto))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Apple Client Secret 토큰이 정상적으로 조회 되어야 한다.")
    @Test
    void getAppleClientSecret() throws Exception {
        // given
        String path = "/app/oauth/apple/client-secret";

        AppleClientSecretResponseDto appleClientSecretResponseDto = AppleClientSecretResponseDto.builder()
                .clientSecret("test_client_secret")
                .build();

        given(oAuthService.getClientSecret())
                .willReturn(appleClientSecretResponseDto);

        //when & then
        mockMvc
                .perform(get(path))
                .andDo(print())
                .andExpect(status().isOk());
    }
}