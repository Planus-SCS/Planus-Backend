package scs.planus.domain.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import scs.planus.domain.Status;
import scs.planus.domain.member.dto.MemberResponseDto;
import scs.planus.domain.member.dto.MemberUpdateRequestDto;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.infra.redis.RedisService;
import scs.planus.infra.s3.AmazonS3Uploader;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AmazonS3Uploader s3Uploader;

    @Mock
    private RedisService redisService;

    private Member member;

    @BeforeEach
    void init() {
        member = Member.builder()
                .nickname("testNick")
                .description("testDesc")
                .profileImageUrl("testImg")
                .status(Status.ACTIVE)
                .build();

        given(memberRepository.findById(any())).willReturn(Optional.of(member));
    }

    @DisplayName("회원정보를 제대로 응답받아야 한다.")
    @Test
    void getDetail_Success(){
        //when
        MemberResponseDto detail = memberService.getDetail(member.getId());

        //then
        assertThat(detail.getNickname()).isEqualTo(member.getNickname());
        assertThat(detail.getDescription()).isEqualTo(member.getDescription());
        assertThat(detail.getProfileImageUrl()).isEqualTo(member.getProfileImageUrl());
    }

    @DisplayName("프로필 변경이 제대로 이루어져야 한다.")
    @Test
    void update_Success(){
        //given
        MockMultipartFile file = new MockMultipartFile("testImg", "test.png", "image/png", "test".getBytes());
        MemberUpdateRequestDto requestDto = MemberUpdateRequestDto.builder()
                .nickname("newNick")
                .description("newDesc")
                .profileImageRemove(false)
                .build();

        given(s3Uploader.updateImage(any(), any(), any()))
                .willReturn("newImageUrl");

        //when
        memberService.update(member.getId(), file, requestDto);

        //then
        assertThat(member.getNickname()).isEqualTo(requestDto.getNickname());
        assertThat(member.getDescription()).isEqualTo(requestDto.getDescription());
        assertThat(member.getProfileImageUrl()).isEqualTo("newImageUrl");
    }

    @DisplayName("이미지를 추가하더라도 프로필 제거하기를 클릭하는 경우 해당 프로필 이미지가 제거되어야 한다.")
    @Test
    void update_Success_Delete_New_Image(){
        //given
        MockMultipartFile file = new MockMultipartFile("testImg", "test.png", "image/png", "test".getBytes());
        MemberUpdateRequestDto requestDto = MemberUpdateRequestDto.builder()
                .nickname("newNick")
                .description("newDesc")
                .profileImageRemove(true)
                .build();

        //when
        memberService.update(member.getId(), file, requestDto);

        //then
        assertThat(member.getNickname()).isEqualTo(requestDto.getNickname());
        assertThat(member.getDescription()).isEqualTo(requestDto.getDescription());
        assertThat(member.getProfileImageUrl()).isNull();
    }

    @DisplayName("프로필 제거하기 클릭만 했을 경우, 기존 프로필 이미지를 제거해야 한다.")
    @Test
    void update_Success_Delete_Original_Image(){
        //given
        MemberUpdateRequestDto requestDto = MemberUpdateRequestDto.builder()
                .profileImageRemove(true)
                .build();

        //when
        memberService.update(member.getId(), null, requestDto);

        //then
        verify(s3Uploader).deleteImage(any());
        assertThat(member.getProfileImageUrl()).isNull();
    }

    @DisplayName("회원 탈퇴시, status가 INACTIVE로 변경되어야 한다.")
    @Test
    void delete_Success(){
        //when
        memberService.delete(member.getId());

        //then
        verify(redisService).delete(any());
        assertThat(member.getStatus()).isEqualTo(Status.INACTIVE);
    }
}