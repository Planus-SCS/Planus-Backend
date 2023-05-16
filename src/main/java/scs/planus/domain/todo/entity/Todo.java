package scs.planus.domain.todo.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.BaseTimeEntity;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.group.entity.Group;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Todo extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    private String title;

    private String description;

    private LocalTime startTime;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean showDDay;

    private boolean completion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_category_id")
    private TodoCategory todoCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    public Todo(String title, String description, LocalTime startTime, LocalDate startDate, LocalDate endDate,
                boolean showDDay, boolean completion, TodoCategory todoCategory, Group group) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.startDate = startDate;
        this.endDate = endDate;
        if (endDate == null) {
            this.endDate = startDate;
        }
        this.showDDay = showDDay;
        this.completion = completion;
        this.todoCategory = todoCategory;
        this.group = group;
    }

    public void update(String title, String description, LocalTime startTime, LocalDate startDate, LocalDate endDate,
                       TodoCategory todoCategory, Group group) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.startDate = startDate;
        this.endDate = endDate;
        if (endDate == null) {
            this.endDate = startDate;
        }
        this.todoCategory = todoCategory;
        this.group = group;
    }

    public void changeCompletion() {
        this.completion =  !this.completion;
    }
}
