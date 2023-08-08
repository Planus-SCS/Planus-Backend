package scs.planus.domain.group.entity;

import lombok.*;
import scs.planus.domain.BaseTimeEntity;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;
import scs.planus.global.exception.PlanusException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static scs.planus.global.exception.CustomExceptionStatus.NOT_EXIST_LEADER;

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

    @Column(columnDefinition = "TEXT")
    private String notice;

    @Column(columnDefinition = "TEXT")
    private String groupImageUrl;

    private int limitCount;

    @Enumerated(EnumType.STRING)
    private GroupScope scope;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupTag> groupTags = new ArrayList<>();

    @Builder
    public Group(String name, String notice, String groupImageUrl, int limitCount, GroupScope scope, Status status) {
        this.name = name;
        this.notice = notice;
        this.groupImageUrl = groupImageUrl;
        this.limitCount = limitCount;
        this.scope = scope;
        this.status = status;
    }

    public static Group creatGroup( String name, String notice, int limitCount, String groupImageUrl ) {
        return Group.builder()
                .name( name )
                .notice( notice )
                .limitCount( limitCount )
                .groupImageUrl( groupImageUrl )
                .scope( GroupScope.PUBLIC )
                .status( Status.ACTIVE )
                .build();
    }

    public Member getLeader() {
        return this.getGroupMembers().stream()
                .filter( GroupMember::isLeader )
                .findFirst()
                .orElseThrow(() -> new PlanusException(NOT_EXIST_LEADER))
                .getMember();
    }

    public void updateDetail(int limitCount, String groupImageUrl ) {
        this.limitCount = limitCount;
        this.groupImageUrl = groupImageUrl;
    }

    public void updateNotice( String notice ) {
        this.notice = notice;
    }

    public void changeStatusToInactive() {
        this.status = Status.INACTIVE;
    }

    public void removeGroupTag(GroupTag groupTag) {
        this.getGroupTags().remove(groupTag);
    }

    public int getActiveGroupMembersSize() {
        return (int) this.getGroupMembers().stream()
                .filter(gm -> gm.getStatus().equals(Status.ACTIVE))
                .count();
    }
}
