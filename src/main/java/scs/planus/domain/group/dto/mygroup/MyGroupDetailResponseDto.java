package scs.planus.domain.group.dto.mygroup;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.group.dto.GroupTagResponseDto;
import scs.planus.domain.group.entity.Group;

import java.util.List;

@Getter
@Builder
public class MyGroupDetailResponseDto {

    private Long groupId;
    private String groupImageUrl;
    private String groupName;
    private Boolean isLeader;
    private Boolean isOnline;
    private int onlineCount;
    private int memberCount;
    private int limitCount;
    private String leaderName;
    private String notice;
    private List<GroupTagResponseDto> groupTags;

    public static MyGroupDetailResponseDto of(Group group, List<GroupTagResponseDto> eachGroupTagDtos,
                                              Boolean isLeader, Boolean onlineStatus, int onlineCount) {
        return MyGroupDetailResponseDto.builder()
                .groupId(group.getId())
                .groupImageUrl(group.getGroupImageUrl())
                .groupName(group.getName())
                .isLeader(isLeader)
                .isOnline(onlineStatus)
                .onlineCount(onlineCount)
                .memberCount(group.getGroupMembers().size()) // 추가쿼리 발생
                .limitCount(group.getLimitCount())
                .leaderName(group.getLeaderName()) // 추가쿼리 발생
                .notice(group.getNotice())
                .groupTags(eachGroupTagDtos)
                .build();
    }
}