package scs.planus.domain.group.dto.mygroup;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.group.dto.GroupTagResponseDto;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;

import java.util.List;

@Getter
@Builder
public class MyGroupDetailResponseDto {

    private Long groupId;
    private String groupImageUrl;
    private String groupName;
    private Boolean isLeader;
    private Boolean hasTodoAuthority;
    private Boolean isOnline;
    private int onlineCount;
    private int memberCount;
    private int limitCount;
    private String leaderName;
    private String notice;
    private List<GroupTagResponseDto> groupTags;

    public static MyGroupDetailResponseDto of(Group group, GroupMember groupMember,
                                              int onlineCount, List<GroupTagResponseDto> eachGroupTagDtos) {
        return MyGroupDetailResponseDto.builder()
                .groupId(group.getId())
                .groupImageUrl(group.getGroupImageUrl())
                .groupName(group.getName())
                .isLeader(groupMember.isLeader())
                .hasTodoAuthority(groupMember.isTodoAuthority())
                .isOnline(groupMember.isOnlineStatus())
                .onlineCount(onlineCount)
                .memberCount(group.getActiveGroupMembersSize()) // 추가쿼리 발생
                .limitCount(group.getLimitCount())
                .leaderName(group.getLeader().getNickname()) // 추가쿼리 발생
                .notice(group.getNotice())
                .groupTags(eachGroupTagDtos)
                .build();
    }
}
