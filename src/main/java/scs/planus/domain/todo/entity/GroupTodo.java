package scs.planus.domain.todo.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.group.entity.Group;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("GT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupTodo extends Todo {

    @OneToMany(mappedBy = "groupTodo")
    List<GroupTodoCompletion> groupTodoCompletions = new ArrayList<>();

    @Builder
    public GroupTodo(String title, String description, LocalTime startTime, LocalDate startDate, LocalDate endDate, boolean showDDay, boolean completion,
                     TodoCategory todoCategory, Group group, List<GroupTodoCompletion> groupTodoCompletions) {
        super(title, description, startTime, startDate, endDate, showDDay, completion, todoCategory, group);
        this.groupTodoCompletions = groupTodoCompletions;
    }
}
