package scs.planus.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface TodoCategoryRepository extends JpaRepository<TodoCategory, Long> {

    @Query("select mc from MemberTodoCategory mc " +
            "where mc.id = :categoryId and mc.member= :member and mc.status = 'ACTIVE'")
    Optional<MemberTodoCategory> findByIdAndMember(@Param("categoryId") Long categoryId,
                                                   @Param("member") Member member);

    @Query("select mc from MemberTodoCategory mc " +
            "where mc.member= :member")
    List<MemberTodoCategory> findAllByMember(@Param("member") Member member);

    @Query("select gc from GroupTodoCategory gc " +
            "where gc.group= :group")
    List<GroupTodoCategory> findAllByGroup(@Param("group") Group group);

    @Query("select gc from GroupTodoCategory gc " +
            "where gc.id= :categoryId " +
            "and gc.status= 'ACTIVE' ")
    Optional<GroupTodoCategory> findByIdAndStatus(@Param("categoryId") Long categoryId);
}
