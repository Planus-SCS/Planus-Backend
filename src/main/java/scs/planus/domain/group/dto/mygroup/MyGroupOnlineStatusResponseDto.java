package scs.planus.domain.group.dto.mygroup;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.group.entity.GroupMember;

@Getter
@Builder
public class MyGroupOnlineStatusResponseDto {

    private Long groupMemberId;

    public static MyGroupOnlineStatusResponseDto of(GroupMember groupMember) {
        return MyGroupOnlineStatusResponseDto.builder()
                .groupMemberId(groupMember.getId())
                .build();
    }
}
