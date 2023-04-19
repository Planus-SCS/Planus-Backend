package scs.planus.domain.group.entity;

import lombok.*;
import scs.planus.domain.BaseTimeEntity;
import scs.planus.domain.tag.entity.Tag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupTag extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    public GroupTag( Group group, Tag tag ) {
        this.group = group;
        if (group != null) { group.getGroupTags().add(this); }
        this.tag = tag;
    }

    public static List<GroupTag> create(Group group, List<Tag> tagList ) {
        return tagList.stream()
                .map( tag -> GroupTag.builder()
                                .group( group )
                                .tag( tag )
                                .build() )
                .collect( Collectors.toList() );
    }
}
