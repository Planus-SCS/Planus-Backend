package scs.planus.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import scs.planus.domain.Status;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.MemberTodo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static scs.planus.domain.category.entity.QTodoCategory.todoCategory;
import static scs.planus.domain.group.entity.QGroup.group;
import static scs.planus.domain.member.entity.QMember.member;
import static scs.planus.domain.todo.entity.QGroupTodo.groupTodo;
import static scs.planus.domain.todo.entity.QMemberTodo.memberTodo;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<MemberTodo> findOneMemberTodoById(Long todoId, Long memberId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(memberTodo)
                .join(memberTodo.member, member)
                .leftJoin(memberTodo.group, group).fetchJoin()
                .join(memberTodo.todoCategory, todoCategory).fetchJoin()
                .where(memberTodo.id.eq(todoId), memberIdEq(memberId))
                .fetchOne());
    }

    public List<MemberTodo> findMemberPeriodTodosByDate(Long memberId, LocalDate from, LocalDate to) {
        return queryFactory
                .selectFrom(memberTodo)
                .join(memberTodo.member, member).fetchJoin()
                .join(memberTodo.todoCategory, todoCategory).fetchJoin()
                .where(memberIdEq(memberId), periodBetween(from, to))
                .orderBy(memberTodo.startDate.asc())
                .fetch();
    }

    public List<MemberTodo> findMemberDailyTodosByDate(Long memberId, LocalDate date) {
        return queryFactory
                .selectFrom(memberTodo)
                .join(memberTodo.member, member)
                .leftJoin(memberTodo.group, group).fetchJoin()
                .join(memberTodo.todoCategory, todoCategory).fetchJoin()
                .where(memberIdEq(memberId), dateBetween(date))
                .orderBy(memberTodo.startTime.asc())
                .fetch();
    }

    public List<MemberTodo> findMemberDailyTodosByDate(Long memberId, Long groupId, LocalDate date) {
        return queryFactory
                .selectFrom(memberTodo)
                .join(memberTodo.member, member)
                .leftJoin(memberTodo.group, group).fetchJoin()
                .join(memberTodo.todoCategory, todoCategory).fetchJoin()
                .where(memberIdEq(memberId), groupIdEq(groupId), dateBetween(date))
                .orderBy(memberTodo.startTime.asc())
                .fetch();
    }

    public List<MemberTodo> findPeriodGroupTodosDetailByDate(Long memberId, LocalDate from, LocalDate to) {
        return queryFactory
                .selectFrom(memberTodo)
                .join(memberTodo.member, member)
                .leftJoin(memberTodo.group, group).fetchJoin()
                .join(memberTodo.todoCategory, todoCategory).fetchJoin()
                .where(memberIdEq(memberId), periodBetween(from, to))
                .orderBy(memberTodo.startDate.asc())
                .fetch();
    }

    public List<MemberTodo> findPeriodGroupTodosByDate(Long memberId, Long groupId, LocalDate from, LocalDate to) {
        return queryFactory
                .selectFrom(memberTodo)
                .join(memberTodo.member, member)
                .leftJoin(memberTodo.group, group).fetchJoin()
                .join(memberTodo.todoCategory, todoCategory).fetchJoin()
                .where(memberIdEq(memberId), groupIdEq(groupId), periodBetween(from, to))
                .orderBy(memberTodo.startDate.asc())
                .fetch();
    }

    /**
     * Query For GroupTodo
     */
    public Optional<GroupTodo> findOneGroupTodoById(Long groupId, Long todoId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(groupTodo)
                .join(groupTodo.group, group).fetchJoin()
                .join(groupTodo.todoCategory, todoCategory).fetchJoin()
                .where(isActiveGroup(), groupTodo.id.eq(todoId), groupIdEq(groupId))
                .fetchOne());
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return member.id.eq(memberId);
    }

    private BooleanExpression groupIdEq(Long groupId) {
        return group.id.eq(groupId);
    }

    private BooleanExpression isActiveGroup() {
        return group.status.eq(Status.ACTIVE);
    }

    private BooleanExpression dateBetween(LocalDate date) {
        return memberTodo.startDate.loe(date).and(memberTodo.endDate.goe(date));
    }

    private BooleanExpression periodBetween(LocalDate from, LocalDate to) {
        return memberTodo.startDate.between(from, to).or(memberTodo.endDate.between(from, to));
    }
}
