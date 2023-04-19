package scs.planus.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.member.entity.Member;

@Getter
@Builder
public class MemberResponseDto {

    private Long memberId;
    private String nickname;
    private String description;
    private String profileImageUrl;

    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .description(member.getDescription())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
