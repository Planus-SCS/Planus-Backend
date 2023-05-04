package scs.planus.domain.group.dto.mygroup;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.group.entity.Group;

@Getter
@Builder
public class GroupBelongInResponseDto {

    private Long groupId;
    private String groupName;

    public static GroupBelongInResponseDto of(Group group) {
        return GroupBelongInResponseDto
                .builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .build();
    }
}
