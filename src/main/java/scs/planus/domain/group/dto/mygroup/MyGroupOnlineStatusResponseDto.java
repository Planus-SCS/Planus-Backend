package scs.planus.domain.group.dto.mygroup;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.group.entity.GroupMember;

@Getter
@Builder
public class MyGroupOnlineStatusResponseDto {

    private Long memberId;

    public static MyGroupOnlineStatusResponseDto of(GroupMember groupMember) {
        return MyGroupOnlineStatusResponseDto.builder()
                .memberId(groupMember.getMember().getId())
                .build();
    }
}
