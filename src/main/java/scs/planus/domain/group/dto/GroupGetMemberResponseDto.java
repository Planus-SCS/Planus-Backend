package scs.planus.domain.group.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.member.entity.Member;

@Getter
@Builder
public class GroupGetMemberResponseDto {
    private Long memberId;
    private String nickname;
    private boolean leader;
    private String description;
    private String profileImageUrl;

    public static GroupGetMemberResponseDto of( Member member, boolean isLeader ) {
        return GroupGetMemberResponseDto.builder()
                .memberId( member.getId() )
                .nickname( member.getNickname() )
                .leader( isLeader )
                .description( member.getDescription() )
                .profileImageUrl( member.getProfileImageUrl() )
                .build();
    }
}
