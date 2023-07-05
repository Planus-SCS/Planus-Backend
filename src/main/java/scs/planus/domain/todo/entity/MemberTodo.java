package scs.planus.domain.todo.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.member.entity.Member;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@DiscriminatorValue("MT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTodo extends Todo{

    private boolean completion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public MemberTodo(String title, String description, LocalTime startTime, LocalDate startDate, LocalDate endDate,
                      boolean isGroupTodo, TodoCategory todoCategory, Group group, boolean completion, Member member) {
        super(title, description, startTime, startDate, endDate, isGroupTodo, todoCategory, group);
        this.completion = completion;
        this.member = member;
    }

    public void changeCompletion() {
        this.completion = !this.completion;
    }
}
