package scs.planus.domain.group.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.member.entity.Member;

@Getter
@Builder
public class GroupGetMemberResponseDto {
    private Long memberId;
    private String nickname;
    private Boolean isLeader;
    private String description;
    private String profileImageUrl;

    public static GroupGetMemberResponseDto of( Member member, Boolean isLeader ) {
        return GroupGetMemberResponseDto.builder()
                .memberId( member.getId() )
                .nickname( member.getNickname() )
                .isLeader( isLeader )
                .description( member.getDescription() )
                .profileImageUrl( member.getProfileImageUrl() )
                .build();
    }
}
