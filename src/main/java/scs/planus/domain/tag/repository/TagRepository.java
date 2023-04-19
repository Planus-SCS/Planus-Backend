package scs.planus.domain.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scs.planus.domain.tag.entity.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
