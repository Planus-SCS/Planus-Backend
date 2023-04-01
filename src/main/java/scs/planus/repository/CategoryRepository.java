package scs.planus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import scs.planus.domain.Member;
import scs.planus.domain.Status;
import scs.planus.domain.TodoCategory;

import java.util.List;

public interface CategoryRepository extends JpaRepository<TodoCategory, Long> {
    @Query("select c from TodoCategory c where c.status= :status and c.member= :member")
    List<TodoCategory> findAllByStatus(@Param("status") Status status, @Param("member") Member member);
}
