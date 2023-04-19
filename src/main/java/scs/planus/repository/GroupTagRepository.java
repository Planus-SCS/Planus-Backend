package scs.planus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import scs.planus.domain.Group;
import scs.planus.domain.GroupTag;

import java.util.List;

public interface GroupTagRepository extends JpaRepository<GroupTag, Long> {
    @Query("select gt " +
            "from GroupTag gt " +
            "join fetch gt.tag t " +
            "where gt.group= :group ")
    List<GroupTag> findAllByGroupId(Group group);
}
