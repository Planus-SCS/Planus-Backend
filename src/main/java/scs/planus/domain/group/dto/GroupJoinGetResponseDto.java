package scs.planus.domain.group.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.group.entity.GroupJoin;

@Getter
@Builder
public class GroupJoinGetResponseDto {
    private Long groupJoinId;
    private Long groupId;
    private String groupName;
    private Long memberId;
    private String memberName;
    private String memberDescription;
    private String memberProfileImageUrl;
    private String acceptStatus;

    public static GroupJoinGetResponseDto of( GroupJoin groupJoin ) {
        return GroupJoinGetResponseDto.builder()
                .groupJoinId( groupJoin.getId() )
                .groupId( groupJoin.getGroup().getId() )
                .groupName( groupJoin.getGroup().getName() )
                .memberId( groupJoin.getMember().getId() )
                .memberName( groupJoin.getMember().getNickname() )
                .memberDescription( groupJoin.getMember().getDescription() )
                .memberProfileImageUrl( groupJoin.getMember().getProfileImageUrl() )
                .acceptStatus( groupJoin.getStatus().toString() )
                .build();
    }
}
