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
    private int onlineCount;
    private int memberCount;
    private int limitCount;
    private String leaderName;
    private List<GroupTagResponseDto> groupTags;

    public static MyGroupResponseDto of(Group group, List<GroupTagResponseDto> eachGroupTagDtos, Boolean onlineStatus, int onlineCount) {
        return MyGroupResponseDto.builder()
                .groupId(group.getId())
                .groupImageUrl(group.getGroupImageUrl())
                .groupName(group.getName())
                .isOnline(onlineStatus)
                .onlineCount(onlineCount)
                .memberCount(group.getActiveGroupMembersSize()) // 추가쿼리 발생
                .limitCount(group.getLimitCount())
                .leaderName(group.getLeader().getNickname()) // 추가쿼리 발생
                .groupTags(eachGroupTagDtos)
                .build();
    }
}
