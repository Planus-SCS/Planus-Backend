package scs.planus.domain.group.dto.mygroup;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.group.dto.GroupTagResponseDto;
import scs.planus.domain.group.entity.Group;

import java.util.List;

@Getter
@Builder
public class MyGroupResponseDto {

    private Long groupId;
    private String groupImageUrl;
    private String groupName;
    private Boolean isOnline;
    private Long onlineCount;
    private Long totalCount;
    private Long limitCount;
    private String leaderName;
    private List<GroupTagResponseDto> groupTags;

    public static MyGroupResponseDto of(Group group, List<GroupTagResponseDto> eachGroupTagDtos, Boolean onlineStatus, Long onlineCount) {
        return MyGroupResponseDto.builder()
                .groupId(group.getId())
                .groupImageUrl(group.getGroupImageUrl())
                .groupName(group.getName())
                .isOnline(onlineStatus)
                .onlineCount(onlineCount)
                .totalCount((long) group.getGroupMembers().size())
                .limitCount(group.getLimitCount())
                .leaderName(group.getLeaderName())
                .groupTags(eachGroupTagDtos)
                .build();
    }
}
