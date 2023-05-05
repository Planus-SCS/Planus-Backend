package scs.planus.domain.group.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import scs.planus.domain.group.entity.Group;

import java.util.List;

@Getter
@Builder
@Slf4j
public class GroupGetResponseDto {
    private Long id;
    private String name;
    private Boolean isJoined;
    private String notice;
    private String groupImageUrl;
    private Integer memberCount;
    private Long limitCount;
    private String leaderName;
    private List<GroupTagResponseDto> groupTags;


    public static GroupGetResponseDto of( Group group, List<GroupTagResponseDto> groupTagNameList, Boolean isJoined ) {
        return GroupGetResponseDto.builder()
                .id( group.getId() )
                .name( group.getName() )
                .isJoined( isJoined )
                .notice( group.getNotice() )
                .groupImageUrl( group.getGroupImageUrl() )
                .memberCount( group.getGroupMembers().size() )
                .limitCount( group.getLimitCount() )
                .leaderName( group.getLeaderName() )
                .groupTags( groupTagNameList )
                .build();
    }
}
