package scs.planus.dto.group;

import lombok.Builder;
import lombok.Getter;
import scs.planus.common.exception.PlanusException;
import scs.planus.common.response.CustomResponseStatus;
import scs.planus.domain.Group;
import scs.planus.domain.GroupMember;
import scs.planus.dto.tag.GroupTagResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GroupGetResponseDto {
    private Long id;
    private String name;
    private String notice;
    private String groupImageUrl;
    private Integer memberCount;
    private Long limitCount;
    private String leaderName;
    private List<GroupTagResponseDto> groupTags;

    public static GroupGetResponseDto of(Group group ) {

        List<GroupTagResponseDto> groupTagNameList = group.getGroupTags().stream()
                                                        .map( GroupTagResponseDto::of )
                                                        .collect( Collectors.toList() );

        String leaderName = findLeader(group);

        return GroupGetResponseDto.builder()
                .id( group.getId() )
                .name( group.getName() )
                .notice( group.getNotice() )
                .groupImageUrl( group.getGroupImageUrl() )
                .memberCount( group.getGroupMembers().size() )
                .limitCount( group.getLimitCount() )
                .leaderName( leaderName )
                .groupTags( groupTagNameList )
                .build();
    }

    private static String findLeader( Group group ) {
        return group.getGroupMembers().stream()
                .filter( GroupMember::isLeader )
                .findFirst()
                .map( gm -> gm.getMember().getNickname() )
                .orElseThrow( () ->
                        new PlanusException(CustomResponseStatus.NOT_EXIST_LEADER)
                );
    }
}
