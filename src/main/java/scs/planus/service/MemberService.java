package scs.planus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import scs.planus.common.exception.PlanusException;
import scs.planus.domain.Member;
import scs.planus.dto.member.MemberResponseDto;
import scs.planus.dto.member.MemberUpdateRequestDto;
import scs.planus.infra.AmazonS3Uploader;
import scs.planus.repository.MemberRepository;

import static scs.planus.common.response.CustomResponseStatus.NONE_USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final AmazonS3Uploader s3Uploader;
    private final MemberRepository memberRepository;

    public MemberResponseDto getDetail(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));
        return MemberResponseDto.of(member);
    }

    @Transactional
    public MemberResponseDto update(Long memberId, MultipartFile multipartFile, MemberUpdateRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        String profileImageUrl = member.getProfileImageUrl();
        if (multipartFile != null) {
            profileImageUrl = s3Uploader.upload(multipartFile, "profile");
        }

        member.updateProfile(requestDto.getNickname(), requestDto.getDescription(), profileImageUrl);
        return MemberResponseDto.of(member);
    }
}
