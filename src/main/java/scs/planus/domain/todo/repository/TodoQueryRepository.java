package scs.planus.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import scs.planus.domain.todo.entity.Todo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static scs.planus.domain.category.entity.QTodoCategory.todoCategory;
import static scs.planus.domain.group.entity.QGroup.group;
import static scs.planus.domain.member.entity.QMember.member;
import static scs.planus.domain.todo.entity.QTodo.todo;

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

    public List<Todo> findDailyTodosByDate(Long memberId, LocalDate date) {
        return queryFactory
                .selectFrom(todo)
                .join(todo.member, member)
                .leftJoin(todo.group, group).fetchJoin()
                .where(memberIdEq(memberId), dateBetween(date))
                .fetch();
    }

    private BooleanExpression dateBetween(LocalDate date) {
        return todo.startDate.loe(date).and(todo.endDate.goe(date));
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return member.id.eq(memberId);
    }
}
