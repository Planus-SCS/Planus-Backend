package scs.planus.dto.group;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.Group;

@Getter
@Builder
public class GroupResponseDto {
    private Long id;
    private String name;
    private String notice;
    private String groupImageUrl;
    private Long limitCount;

    public static GroupResponseDto of(Group group) {
        return GroupResponseDto.builder()
                .id(group.getId())
                .name(group.getName())
                .notice(group.getNotice())
                .limitCount(group.getLimitCount())
                .groupImageUrl(group.getGroupImageUrl())
                .build();
    }
}
