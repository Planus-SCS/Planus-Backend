package scs.planus.domain.group.dto.mygroup;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.group.dto.GroupTagResponseDto;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupTag;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MyGroupResponseDto {

    private Long groupId;
    private String groupName;
    private Long totalCount;
    private Long limitCount;
    private Long onlineCount;
    private String leaderName;
    private Boolean isOnline;
    private List<GroupTagResponseDto> groupTags;

    public static MyGroupResponseDto of(Group group, List<GroupTag> groupTags) {
        List<GroupTagResponseDto> groupTagDtos = groupTags.stream()
                .map(GroupTagResponseDto::of)
                .collect(Collectors.toList());

        return MyGroupResponseDto.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .limitCount(group.getLimitCount())
                .leaderName(group.getLeaderName())
                .totalCount((long) group.getGroupMembers().size())
                .groupTags(groupTagDtos)
                .build();
    }
}
