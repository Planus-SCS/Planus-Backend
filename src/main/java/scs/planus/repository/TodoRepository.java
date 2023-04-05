package scs.planus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scs.planus.domain.todo.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
