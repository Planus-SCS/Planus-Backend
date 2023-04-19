package scs.planus.domain.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scs.planus.domain.todo.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
