package scs.planus.domain.group.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scs.planus.domain.group.entity.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("select g from Group g " +
            "where g.status= 'ACTIVE' " +
            "order by g.groupMembers.size desc, g.id desc ")
    List<Group> findAllByActiveOrderByNumOfMembersAndId(Pageable pageable);

    @Query("select distinct g " +
            "from Group g " +
            "join fetch g.groupMembers gm " +
            "where g.id= :groupId " +
            "and g.status= 'ACTIVE'")
    Optional<Group> findWithGroupMemberById(@Param("groupId") Long groupId);

    @Query("select g " +
            "from Group g " +
            "where g.id= :groupId " +
            "and g.status= 'ACTIVE'")
    Optional<Group> findByIdAndStatus(@Param("groupId") Long groupId);
}
