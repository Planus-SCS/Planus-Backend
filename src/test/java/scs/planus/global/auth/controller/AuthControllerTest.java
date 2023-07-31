package scs.planus.global.auth.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import scs.planus.global.auth.dto.TokenReissueRequestDto;
import scs.planus.global.auth.dto.TokenReissueResponseDto;
import scs.planus.global.auth.entity.Token;
import scs.planus.global.auth.service.AuthService;
import scs.planus.support.ControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends ControllerTest {

    @MockBean
    private AuthService authService;

    @DisplayName("토큰을 재발급한다.")
    @Test
    void tokenReissue() throws Exception {
        //given
        String path = "/app/auth/token-reissue";

        TokenReissueRequestDto requestDto = TokenReissueRequestDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        Token token = Token.builder()
                .accessToken("newAccessToken")
                .refreshToken("newRefreshToken")
                .refreshTokenExpiredIn(1L)
                .build();

        given(authService.reissue(any(TokenReissueRequestDto.class)))
                .willReturn(new TokenReissueResponseDto(token));

        //when & then
        mockMvc
                .perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}