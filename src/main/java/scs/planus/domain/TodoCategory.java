package scs.planus.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoCategory extends BaseTimeEntity{

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

    //== 연관관계 편의 메소드 ==//
    public void setMember(Member member) {
        this.member = member;
        member.getTodoCategories().add(this);
    }

    @Builder
    public TodoCategory(String name, Color color) {
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
