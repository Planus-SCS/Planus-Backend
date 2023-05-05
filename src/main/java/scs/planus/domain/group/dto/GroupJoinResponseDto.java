package scs.planus.domain.group.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.group.entity.GroupJoin;

@Getter
@Builder
public class GroupJoinResponseDto {
    private Long groupJoinId;

    public static GroupJoinResponseDto of( GroupJoin groupJoin ) {
        return GroupJoinResponseDto.builder()
                .groupJoinId( groupJoin.getId() )
                .build();
    }
}
