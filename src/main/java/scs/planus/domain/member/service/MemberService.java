package scs.planus.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import scs.planus.infra.redis.RedisService;
import scs.planus.global.exception.PlanusException;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.dto.MemberResponseDto;
import scs.planus.domain.member.dto.MemberUpdateRequestDto;
import scs.planus.infra.s3.AmazonS3Uploader;
import scs.planus.domain.member.repository.MemberRepository;

import static scs.planus.global.exception.CustomExceptionStatus.NONE_USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final RedisService redisService;
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

        String oldProfileImageUrl = member.getProfileImageUrl();
        String newProfileImageUrl = updateOrDeleteImage(multipartFile, oldProfileImageUrl, requestDto);

        member.updateProfile(requestDto.getNickname(), requestDto.getDescription(), newProfileImageUrl);
        return MemberResponseDto.of(member);
    }

    @Transactional
    public MemberResponseDto delete(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));
        member.changeStatusToInactive();
        redisService.delete(member.getEmail());
        return MemberResponseDto.of(member);
    }

    private String updateOrDeleteImage(MultipartFile multipartFile, String oldProfileImageUrl, MemberUpdateRequestDto requestDto) {
        if (requestDto.isProfileImageRemove()) {
            s3Uploader.deleteImage(oldProfileImageUrl);
            return null;
        }

        return s3Uploader.updateImage(multipartFile, oldProfileImageUrl, "members");
    }
}