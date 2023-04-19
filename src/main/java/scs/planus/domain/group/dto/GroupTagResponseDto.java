package scs.planus.domain.group.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.group.entity.GroupTag;

@Getter
@Builder
public class GroupTagResponseDto {
    private Long id;
    private String name;

    public static GroupTagResponseDto of( GroupTag groupTag ) {
        return GroupTagResponseDto.builder()
                .id( groupTag.getTag().getId() )
                .name( groupTag.getTag().getName() )
                .build();
    }
}
