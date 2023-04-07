package scs.planus.domain.todo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.Member;
import scs.planus.domain.TodoCategory;

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
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTodo extends Todo{

    private boolean completion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public MemberTodo(Long id, String title, String description, LocalTime startTime,
                      LocalDate startDate, LocalDate endDate, boolean showDDay, TodoCategory todoCategory, boolean completion, Member member) {
        super(id, title, description, startTime, startDate, endDate, showDDay, todoCategory);
        this.completion = completion;
        this.member = member;
    }
}
