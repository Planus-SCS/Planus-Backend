package scs.planus.domain.todo.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.member.entity.Member;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupTodoCompletion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_todo_completion_id")
    private Long id;

    private boolean completion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private GroupTodo groupTodo;

    @Builder
    public GroupTodoCompletion(boolean completion, Member member, GroupTodo groupTodo) {
        this.completion = completion;
        this.member = member;
        if (groupTodo != null) {
            groupTodo.getGroupTodoCompletions().add(this);
        }
        this.groupTodo = groupTodo;
    }
}
