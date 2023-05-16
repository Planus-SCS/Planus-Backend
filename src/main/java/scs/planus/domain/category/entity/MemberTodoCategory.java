package scs.planus.domain.category.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.member.entity.Member;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("MC")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTodoCategory extends TodoCategory {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public MemberTodoCategory(Member member, String name, Color color) {
        super(name, color);
        this.member = member;
        if (member != null) {
            member.getTodoCategories().add(this);
        }
    }
}
