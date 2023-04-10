package scs.planus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scs.planus.domain.todo.Todo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Optional<Todo> findByIdAndMemberId(Long id, Long memberId);

    @Query("select t from Todo t " +
            "join fetch t.member m " +
            "where m.id = :memberId and (t.startDate <= :date and t.endDate >= :date)")
    List<Todo> findAllByMemberIdAndDate(@Param("memberId") Long memberId,
                                        @Param("date") LocalDate date);
}
