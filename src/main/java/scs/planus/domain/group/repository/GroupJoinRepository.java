package scs.planus.domain.group.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupJoin;

import java.util.List;
import java.util.Optional;

public interface GroupJoinRepository extends JpaRepository<GroupJoin, Long> {

    @Query("select gj " +
            "from GroupJoin gj " +
            "join fetch gj.group " +
            "join fetch gj.member " +
            "where gj.group in :groups ")
    List<GroupJoin> findAllByGroupIn(@Param("groups") List<Group> groups);

    @Query("select gj from GroupJoin gj " +
            "join fetch gj.group " +
            "join fetch gj.member " +
            "where gj.id= :groupJoinId " +
            "and gj.status = 'INACTIVE'")
    Optional<GroupJoin> findWithGroupById(@Param("groupJoinId") Long groupJoinId );
}
