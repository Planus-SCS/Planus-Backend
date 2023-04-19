package scs.planus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import scs.planus.domain.Group;
import scs.planus.domain.GroupMember;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("select gm " +
            "from GroupMember gm " +
            "join fetch gm.member m " +
            "where gm.group= :group " +
            "and gm.leader= true ")
    Optional<GroupMember> findLeaderByGroup(Group group);
}
