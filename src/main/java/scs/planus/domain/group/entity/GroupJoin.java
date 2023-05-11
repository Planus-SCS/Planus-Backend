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
public class GroupJoin extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_join_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    public GroupJoin( Member member, Group group ) {
        this.status = Status.INACTIVE;
        this.member = member;
        this.group = group;
    }

    public static GroupJoin createGroupJoin(Member member, Group group ) {
        return GroupJoin.builder()
                .member( member )
                .group( group )
                .build();
    }
}
