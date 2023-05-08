package scs.planus.domain.group.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.group.entity.GroupMember;

@Getter
@Builder
public class GroupMemberResponseDto {
    private Long groupMemberId;

    public static GroupMemberResponseDto of(GroupMember groupMember ) {
        return GroupMemberResponseDto.builder()
                .groupMemberId( groupMember.getId() )
                .build();
    }
}
