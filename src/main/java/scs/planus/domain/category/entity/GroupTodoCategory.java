package scs.planus.domain.category.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.group.entity.Group;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("GC")
@Getter
@NoArgsConstructor
public class GroupTodoCategory extends TodoCategory {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    public GroupTodoCategory(String name, Color color, Group group) {
        super(name, color);
        this.group = group;
    }
}
