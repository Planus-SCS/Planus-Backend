package scs.planus.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "_Group")
public class Group extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    private String name;

    private String introduction;

    private String notice;

    private String groupImageUrl;

    private Long limitCount;

    @Enumerated(EnumType.STRING)
    private GroupScope scope;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @Builder
    public Group(String name, String notice, String groupImageUrl, Long limitCount, GroupScope scope, Status status) {
        this.name = name;
        this.notice = notice;
        this.groupImageUrl = groupImageUrl;
        this.limitCount = limitCount;
        this.scope = scope;
        this.status = status;
    }

    public static Group creatGroup(String name, String notice, Long limitCount, String groupImageUrl) {
        return Group.builder()
                .name(name)
                .notice(notice)
                .limitCount(limitCount)
                .groupImageUrl(groupImageUrl)
                .scope(GroupScope.PUBLIC)
                .status(Status.ACTIVE)
                .build();
    }
}
