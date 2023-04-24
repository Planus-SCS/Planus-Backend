package scs.planus.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scs.planus.domain.group.entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
}
