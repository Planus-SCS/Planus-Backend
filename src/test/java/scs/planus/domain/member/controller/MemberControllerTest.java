package scs.planus.domain.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import scs.planus.domain.member.dto.MemberResponseDto;
import scs.planus.domain.member.dto.MemberUpdateRequestDto;
import scs.planus.domain.member.service.MemberService;
import scs.planus.support.ControllerTest;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends ControllerTest {

    @MockBean
    private MemberService memberService;

    @DisplayName("멤버 정보가 조회되어야 한다.")
    @Test
    void showDetail() throws Exception {
        //given
        String path = "/app/members";

        MemberResponseDto responseDto = MemberResponseDto.builder()
                .memberId(1L)
                .nickname("planus")
                .build();

        given(memberService.getDetail(anyLong()))
                .willReturn(responseDto);

        //when & then
        mockMvc
                .perform(get(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("멤버 정보가 수정되어야 한다.")
    @Test
    void update() throws Exception {
        //given
        String path = "/app/members";

        MockMultipartFile image = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());

        MemberUpdateRequestDto requestDto = MemberUpdateRequestDto.builder().nickname("newNick").build();
        String dtoToJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile updateRequestDto = new MockMultipartFile("updateRequestDto", "", "application/json", dtoToJson.getBytes(StandardCharsets.UTF_8));

        MemberResponseDto responseDto = MemberResponseDto.builder()
                .memberId(1L)
                .nickname(requestDto.getNickname())
                .profileImageUrl(image.getOriginalFilename())
                .build();

        given(memberService.update(anyLong(), any(MockMultipartFile.class), any(MemberUpdateRequestDto.class)))
                .willReturn(responseDto);

        //when & then
        mockMvc
                .perform(multipart(HttpMethod.PATCH, path)
                        .file(image)
                        .file(updateRequestDto)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("멤버 정보 수정시, 검증조건을 만족하지 못하면 예외를 던진다." +
            " - nickname 10글자 초과" +
            " - description 50글자 초과")
    @Test
    void update_Throw_Exception_If_Not_Validated_Request() throws Exception {
        //given
        String path = "/app/members";

        MemberUpdateRequestDto requestDto = MemberUpdateRequestDto.builder()
                .nickname("A".repeat(11))
                .description("A".repeat(51))
                .build();
        String dtoToJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile updateRequestDto = new MockMultipartFile("updateRequestDto", "", "application/json", dtoToJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc
                .perform(multipart(HttpMethod.PATCH, path)
                        .file(updateRequestDto)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("멤버가 삭제되어야 한다.")
    @Test
    void deleteMember() throws Exception {
        //given
        String path = "/app/members";

        MemberResponseDto responseDto = MemberResponseDto.builder().memberId(1L).build();

        given(memberService.delete(anyLong()))
                .willReturn(responseDto);

        //when & then
        mockMvc
                .perform(delete(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}