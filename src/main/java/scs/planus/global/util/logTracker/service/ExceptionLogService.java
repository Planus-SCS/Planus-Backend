package scs.planus.global.util.logTracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.global.util.logTracker.entity.ExceptionLog;
import scs.planus.global.util.logTracker.repository.ExceptionLogRepository;
import scs.planus.global.util.logTracker.service.dto.ExceptionLogDto;

@Service
@RequiredArgsConstructor
public class ExceptionLogService {
    private final ExceptionLogRepository exceptionLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExceptionLog save(ExceptionLogDto exceptionLogDto) {
        ExceptionLog exceptionLog = exceptionLogDto.toEntity();

        return exceptionLogRepository.save(exceptionLog);
    }
}
