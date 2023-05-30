package scs.planus.domain.group.entity;

import lombok.*;
import scs.planus.domain.BaseTimeEntity;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.Status;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_id")
    private Long id;

    private boolean onlineStatus;

    private boolean leader;

    private boolean todoAuthority;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    public GroupMember(boolean leader, boolean todoAuthority, Member member, Group group) {
        this.onlineStatus = false;
        this.leader = leader;
        this.todoAuthority = todoAuthority;
        this.status = Status.ACTIVE;
        this.member = member;
        this.group = group;
        if (group != null) { group.getGroupMembers().add(this); }
    }

    public static GroupMember creatGroupLeader(Member member, Group group ) {
        return GroupMember.builder()
                .leader(true)
                .todoAuthority(true)
                .member(member)
                .group(group)
                .build();
    }

    public static GroupMember creatGroupMember(Member member, Group group ) {
        return GroupMember.builder()
                .leader(false)
                .todoAuthority(false)
                .member(member)
                .group(group)
                .build();
    }

    public void changeOnlineStatus() {
        this.onlineStatus = !this.onlineStatus;
    }

    public void changeStatusToInactive() {
        this.status = Status.INACTIVE;
    }

    public void changeStatusToActive() {
        this.status = Status.ACTIVE;
    }
}
