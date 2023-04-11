package scs.planus.repository.todo;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import scs.planus.domain.todo.Todo;

import java.util.Optional;

import static scs.planus.domain.QGroup.group;
import static scs.planus.domain.QMember.member;
import static scs.planus.domain.QTodoCategory.todoCategory;
import static scs.planus.domain.todo.QTodo.todo;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Todo> findOneTodoById(Long todoId, Long memberId){
        return Optional.ofNullable(queryFactory
                .selectFrom(todo)
                .join(todo.member, member)
                .leftJoin(todo.group, group).fetchJoin()
                .join(todo.todoCategory, todoCategory).fetchJoin()
                .where(todo.id.eq(todoId), memberIdEq(memberId))
                .fetchOne());
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return member.id.eq(memberId);
    }
}
