package scs.planus.global.util.logTracker.service.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.global.util.logTracker.entity.ExceptionData;
import scs.planus.global.util.logTracker.entity.ExceptionLog;
import scs.planus.global.util.logTracker.entity.MetaData;

@Getter
@Builder
public class ExceptionLogDto {
    private String requestURI;
    private String httpMethod;
    private String email;
    private String className;
    private String methodName;
    private Integer lineNumber;
    private String parameter;
    private String exceptionType;
    private String message;

    public ExceptionLog toEntity() {
        MetaData metaData = new MetaData(requestURI, httpMethod, email, className, methodName, lineNumber, parameter);
        ExceptionData exceptionData = new ExceptionData(exceptionType, message);

        return ExceptionLog.builder()
                .metaData(metaData)
                .exceptionData(exceptionData)
                .build();
    }
}
