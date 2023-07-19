package scs.planus.domain.group.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import scs.planus.domain.Status;

import static scs.planus.domain.group.entity.QGroup.group;
import static scs.planus.domain.group.entity.QGroupMember.groupMember;
import static scs.planus.domain.member.entity.QMember.member;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GroupMemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Boolean existByMemberIdAndGroupId(Long memberId, Long groupId) {
        Integer fetchOne = queryFactory.selectOne()
                .from(groupMember)
                .join(groupMember.member, member)
                .join(groupMember.group, group)
                .where(isActiveGroup(), memberIdEq(memberId), groupIdEq(groupId), groupMember.status.eq(Status.ACTIVE))
                .fetchFirst();
        return fetchOne != null;
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
}
