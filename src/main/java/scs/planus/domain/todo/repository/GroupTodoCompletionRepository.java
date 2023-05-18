package scs.planus.domain.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scs.planus.domain.todo.entity.GroupTodoCompletion;

public interface GroupTodoCompletionRepository extends JpaRepository<GroupTodoCompletion, Long> {
}
