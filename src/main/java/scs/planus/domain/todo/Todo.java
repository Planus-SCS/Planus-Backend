package scs.planus.domain.todo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.BaseTimeEntity;
import scs.planus.domain.Group;
import scs.planus.domain.Member;
import scs.planus.domain.TodoCategory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    private String title;

    private String description;

    private LocalTime startTime;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean showDDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_category_id")
    private TodoCategory todoCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    public Todo(String title, String description, LocalTime startTime, LocalDate startDate, LocalDate endDate,
                boolean showDDay, TodoCategory todoCategory, Member member, Group group) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.startDate = startDate;
        this.endDate = endDate;
        if (endDate == null) {
            this.endDate = startDate;
        }
        this.showDDay = showDDay;
        this.todoCategory = todoCategory;
        this.member = member;
        this.group = group;
    }
}
