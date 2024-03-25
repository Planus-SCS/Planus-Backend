package scs.planus.global.util.logTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scs.planus.global.util.logTracker.entity.ExceptionLog;

public interface ExceptionLogRepository extends JpaRepository<ExceptionLog, Long> {
}
