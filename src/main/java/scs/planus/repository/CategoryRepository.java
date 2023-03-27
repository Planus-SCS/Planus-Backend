package scs.planus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scs.planus.domain.TodoCategory;

public interface CategoryRepository extends JpaRepository<TodoCategory, Long> {
}
