package scs.planus.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.member.entity.Member;

import java.util.List;

public interface CategoryRepository extends JpaRepository<TodoCategory, Long> {
    @Query("select c from TodoCategory c " +
            "where c.member= :member")
    List<TodoCategory> findAllByMember(@Param("member") Member member);
}
