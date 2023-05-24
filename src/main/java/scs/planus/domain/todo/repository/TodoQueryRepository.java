package scs.planus.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import scs.planus.domain.Status;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.entity.Todo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static scs.planus.domain.category.entity.QTodoCategory.todoCategory;
import static scs.planus.domain.group.entity.QGroup.group;
import static scs.planus.domain.member.entity.QMember.member;
import static scs.planus.domain.todo.entity.QGroupTodo.groupTodo;
import static scs.planus.domain.todo.entity.QMemberTodo.memberTodo;
import static scs.planus.domain.todo.entity.QTodo.todo;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * Query For GroupMemberTodo
     */
    public List<Todo> findGroupMemberPeriodTodosByDate(Long memberId, Long groupId, LocalDate from, LocalDate to) {
        return queryFactory
                .selectFrom(todo)
                .join(memberTodo).on(memberTodo.eq(todo))
                .join(todo.group, group).on(groupIdEq(groupId))
                .join(todo.todoCategory, todoCategory).fetchJoin()
                .leftJoin(memberTodo.member, member)
                .where((memberIdEq(memberId).and(groupIdEq(groupId)))
                                .or(groupIdEq(groupId).and(member.isNull())),
                        todoPeriodBetween(from, to))
                .orderBy(todo.startDate.asc(), todo.endDate.desc())
                .fetch();
    }

    public List<Todo> findGroupMemberDailyTodosByDate(Long memberId, Long groupId, LocalDate date) {
        return queryFactory
                .selectFrom(todo)
                .join(memberTodo).on(memberTodo.eq(todo))
                .join(todo.group, group).on(groupIdEq(groupId))
                .join(todo.todoCategory, todoCategory).fetchJoin()
                .leftJoin(memberTodo.member, member)
                .where((memberIdEq(memberId).and(groupIdEq(groupId)))
                                .or(groupIdEq(groupId).and(member.isNull())),
                        todoDateBetween(date))
                .orderBy(todo.startTime.asc())
                .fetch();
    }

    /**
     * Query For MemberTodo
     */
    public Optional<MemberTodo> findOneMemberTodoById(Long todoId, Long memberId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(memberTodo)
                .join(memberTodo.member, member)
                .join(memberTodo.group, group).fetchJoin()
                .join(memberTodo.todoCategory, todoCategory).fetchJoin()
                .where(memberTodo.id.eq(todoId), memberIdEq(memberId))
                .fetchOne());
    }

    public List<MemberTodo> findAllPeriodMemberTodosByDate(Long memberId, LocalDate from, LocalDate to) {
        return queryFactory
                .selectFrom(memberTodo)
                .join(memberTodo.member, member).fetchJoin()
                .join(memberTodo.todoCategory, todoCategory).fetchJoin()
                .where(memberIdEq(memberId), memberTodoPeriodBetween(from, to))
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

    public List<GroupTodo> findPeriodGroupTodosByDate(Long groupId, LocalDate from, LocalDate to) {
        return queryFactory
                .selectFrom(groupTodo)
                .join(groupTodo.group, group)
                .join(groupTodo.todoCategory, todoCategory).fetchJoin()
                .where(groupIdEq(groupId), groupTodoPeriodBetween(from, to))
                .orderBy(groupTodo.startDate.asc(), groupTodo.endDate.desc())
                .fetch();
    }

    public List<GroupTodo> findDailyGroupTodosByDate(Long groupId, LocalDate date) {
        return queryFactory
                .selectFrom(groupTodo)
                .join(groupTodo.group, group)
                .join(groupTodo.todoCategory, todoCategory).fetchJoin()
                .where(groupIdEq(groupId), groupTodoDateBetween(date))
                .orderBy(groupTodo.startTime.asc())
                .fetch();
    }

    public List<GroupTodo> findAllPeriodGroupTodosByDate(List<Group> groups, LocalDate from , LocalDate to) {
        return queryFactory
                .selectFrom(groupTodo)
                .join(groupTodo.group, group).fetchJoin()
                .join(groupTodo.todoCategory, todoCategory).fetchJoin()
                .where(groupsIn(groups), groupTodoPeriodBetween(from, to))
                .orderBy(groupTodo.startDate.asc())
                .fetch();
    }

    private BooleanExpression todoIdEq(Long todoId) {
        return todo.id.eq(todoId);
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return member.id.eq(memberId);
    }

    private BooleanExpression groupIdEq(Long groupId) {
        return group.id.eq(groupId);
    }

    private BooleanExpression groupsIn(List<Group> groups) {
        return group.in(groups);
    }

    private BooleanExpression isActiveGroup() {
        return group.status.eq(Status.ACTIVE);
    }

    // TODO 동일한 기능, 다른 타입을 이용하는 함수가 너무 많음 -> 리팩토링 필요
    private BooleanExpression memberTodoPeriodBetween(LocalDate from, LocalDate to) {
        return memberTodo.startDate.between(from, to).or(memberTodo.endDate.between(from, to));
    }

    private BooleanExpression groupTodoDateBetween(LocalDate date) {
        return groupTodo.startDate.loe(date).and(groupTodo.endDate.goe(date));
    }

    private BooleanExpression groupTodoPeriodBetween(LocalDate from, LocalDate to) {
        return groupTodo.startDate.between(from, to).or(groupTodo.endDate.between(from, to));
    }

    private BooleanExpression todoDateBetween(LocalDate date) {
        return todo.startDate.loe(date).and(todo.endDate.goe(date));
    }

    private BooleanExpression todoPeriodBetween(LocalDate from, LocalDate to) {
        return todo.startDate.between(from, to).or(todo.endDate.between(from, to));
    }
}
