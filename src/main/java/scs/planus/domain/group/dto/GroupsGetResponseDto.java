package scs.planus.domain.group.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.Status;
import scs.planus.domain.group.entity.Group;

import java.util.List;

@Getter
@Builder
public class GroupsGetResponseDto {
    private Long groupId;
    private String name;
    private String groupImageUrl;
    private int memberCount;
    private int limitCount;
    private Long leaderId;
    private String leaderName;
    private List<GroupTagResponseDto> groupTags;

    public static GroupsGetResponseDto of( Group group, List<GroupTagResponseDto> groupTagNameList ) {
        return GroupsGetResponseDto.builder()
                .groupId( group.getId() )
                .name( group.getName() )
                .groupImageUrl( group.getGroupImageUrl() )
                .memberCount( (int) group.getGroupMembers().stream()
                        .filter(gm -> gm.getStatus().equals(Status.ACTIVE))
                        .count() )
                .limitCount( group.getLimitCount() )
                .leaderId( group.getLeader().getId() )
                .leaderName( group.getLeader().getNickname() )
                .groupTags( groupTagNameList )
                .build();
    }
}
