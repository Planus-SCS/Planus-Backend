package scs.planus.domain.group.dto.mygroup;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.group.entity.GroupMember;

@Getter
@Builder
public class MyGroupGetMemberResponseDto {

    private Long memberId;
    private String nickname;
    private Boolean isLeader;
    private Boolean isOnline;
    private String description;
    private String profileImageUrl;

    public static MyGroupGetMemberResponseDto of(GroupMember groupMember) {
        return MyGroupGetMemberResponseDto.builder()
                .memberId(groupMember.getMember().getId())
                .nickname(groupMember.getMember().getNickname())
                .isLeader(groupMember.isLeader())
                .isOnline(groupMember.isOnlineStatus())
                .description(groupMember.getMember().getDescription())
                .profileImageUrl(groupMember.getMember().getProfileImageUrl())
                .build();
    }
}
