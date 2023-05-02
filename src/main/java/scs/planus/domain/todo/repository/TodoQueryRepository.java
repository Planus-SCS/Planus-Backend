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

    public Optional<Todo> findOneTodoById(Long todoId, Long memberId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(todo)
                .join(todo.member, member)
                .leftJoin(todo.group, group).fetchJoin()
                .join(todo.todoCategory, todoCategory).fetchJoin()
                .where(todo.id.eq(todoId), memberIdEq(memberId))
                .fetchOne());
    }

    public List<Todo> findPeriodTodosByDate(Long memberId, LocalDate from, LocalDate to) {
        return queryFactory
                .selectFrom(todo)
                .join(todo.member, member).fetchJoin()
                .join(todo.todoCategory, todoCategory).fetchJoin()
                .where(memberIdEq(memberId), periodBetween(from, to))
                .orderBy(todo.startDate.asc())
                .fetch();
    }

    public List<Todo> findDailyTodosByDate(Long memberId, LocalDate date) {
        return queryFactory
                .selectFrom(todo)
                .join(todo.member, member)
                .leftJoin(todo.group, group).fetchJoin()
                .join(todo.todoCategory, todoCategory).fetchJoin()
                .where(memberIdEq(memberId), dateBetween(date))
                .orderBy(todo.startTime.asc())
                .fetch();
    }

    public List<Todo> findPeriodTodosDetailByDate(Long memberId, LocalDate from, LocalDate to) {
        return queryFactory
                .selectFrom(todo)
                .join(todo.member, member)
                .leftJoin(todo.group, group).fetchJoin()
                .join(todo.todoCategory, todoCategory).fetchJoin()
                .where(memberIdEq(memberId), periodBetween(from, to))
                .orderBy(todo.startDate.asc())
                .fetch();
    }

    public List<Todo> findPeriodTodosDetailByDate(Long memberId, Long groupId, LocalDate from, LocalDate to) {
        return queryFactory
                .selectFrom(todo)
                .join(todo.member, member)
                .leftJoin(todo.group, group).fetchJoin()
                .join(todo.todoCategory, todoCategory).fetchJoin()
                .where(memberIdEq(memberId), groupIdEq(groupId), periodBetween(from, to))
                .orderBy(todo.startDate.asc())
                .fetch();
    }

    private BooleanExpression periodBetween(LocalDate from, LocalDate to) {
        return todo.startDate.between(from, to).or(todo.endDate.between(from, to));
    }

    private BooleanExpression dateBetween(LocalDate date) {
        return todo.startDate.loe(date).and(todo.endDate.goe(date));
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return member.id.eq(memberId);
    }

    private BooleanExpression groupIdEq(Long groupId) {
        return group.id.eq(groupId);
    }
}
