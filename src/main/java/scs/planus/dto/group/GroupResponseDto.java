package scs.planus.dto.group;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.Group;

@Getter
@Builder
public class GroupResponseDto {
    private Long groupId;

    public static GroupResponseDto of(Group group ) {
        return GroupResponseDto.builder()
                .groupId( group.getId() )
                .build();
    }
}
