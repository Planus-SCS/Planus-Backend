package scs.planus.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scs.planus.domain.group.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
