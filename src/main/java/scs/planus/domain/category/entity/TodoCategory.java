package scs.planus.domain.category.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.BaseTimeEntity;
import scs.planus.domain.Status;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TodoCategory extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_category_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Enumerated(EnumType.STRING)
    private Status status;

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
