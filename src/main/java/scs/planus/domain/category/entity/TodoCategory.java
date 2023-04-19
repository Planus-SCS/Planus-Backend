package scs.planus.domain.category.entity;

import lombok.*;
import scs.planus.domain.BaseTimeEntity;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.Status;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoCategory extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String name;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder
    public TodoCategory(Member member, String name, Color color) {
        this.member = member;
        if (member != null) { member.getTodoCategories().add(this); }
        this.name = name;
        this.color = color;
        this.status = Status.ACTIVE;
    }

    public void change(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public void changeStatusToInactive() {
        this.status = Status.INACTIVE;
    }
}
