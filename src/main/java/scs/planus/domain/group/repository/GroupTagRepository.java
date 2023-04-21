package scs.planus.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupTag;

import java.util.List;

public interface GroupTagRepository extends JpaRepository<GroupTag, Long> {
    @Query("select gt " +
            "from GroupTag gt " +
            "join fetch gt.tag t " +
            "where gt.group= :group ")
    List<GroupTag> findAllByGroup(@Param("group") Group group);
}
