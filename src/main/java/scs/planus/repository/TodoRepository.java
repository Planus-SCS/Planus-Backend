package scs.planus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scs.planus.domain.todo.Todo;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Optional<Todo> findByIdAndMemberId(Long id, Long memberId);
}
