package scs.planus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scs.planus.domain.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
