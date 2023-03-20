package scs.planus.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import scs.planus.common.response.BaseResponse;
import scs.planus.common.response.ResponseStatus;

@RestControllerAdvice
public class PlanusExceptionHandler {

    @ExceptionHandler(PlanusException.class)
    public ResponseEntity<Object> handlePlanusException(PlanusException e) {
        ResponseStatus status = e.getStatus();
        return ResponseEntity.status(status.getHttpStatus())
                .body(new BaseResponse<>(status));
    }
}
