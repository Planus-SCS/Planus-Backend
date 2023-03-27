package scs.planus.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoCategory extends BaseTimeEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_category_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Builder
    public TodoCategory(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public void change(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}
