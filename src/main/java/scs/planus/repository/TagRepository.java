package scs.planus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scs.planus.domain.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
