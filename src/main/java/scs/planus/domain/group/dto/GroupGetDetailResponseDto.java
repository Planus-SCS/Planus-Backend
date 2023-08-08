package scs.planus.domain.group.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import scs.planus.domain.group.entity.Group;

import java.util.List;

@Getter
@Builder
@Slf4j
public class GroupGetDetailResponseDto {
    private Long id;
    private String name;
    private Boolean isJoined;
    private String notice;
    private String groupImageUrl;
    private int memberCount;
    private int limitCount;
    private String leaderName;
    private List<GroupTagResponseDto> groupTags;


    public static GroupGetDetailResponseDto of(Group group, List<GroupTagResponseDto> groupTagNameList, Boolean isJoined ) {
        return GroupGetDetailResponseDto.builder()
                .id( group.getId() )
                .name( group.getName() )
                .isJoined( isJoined )
                .notice( group.getNotice() )
                .groupImageUrl( group.getGroupImageUrl() )
                .memberCount( group.getActiveGroupMembersSize() )
                .limitCount( group.getLimitCount() )
                .leaderName( group.getLeader().getNickname() )
                .groupTags( groupTagNameList )
                .build();
    }
}
