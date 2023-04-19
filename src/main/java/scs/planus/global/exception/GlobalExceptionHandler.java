package scs.planus.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import scs.planus.global.common.response.BaseResponse;
import scs.planus.global.common.response.ResponseStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlanusException.class)
    public ResponseEntity<Object> handlePlanusException(PlanusException e) {
        ResponseStatus status = e.getStatus();
        return ResponseEntity.status(status.getHttpStatus())
                .body(new BaseResponse<>(status));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidException() {
        ResponseStatus status = CustomExceptionStatus.INVALID_PARAMETER;
        return ResponseEntity.status(status.getHttpStatus())
                .body(new BaseResponse<>(status));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonException() {
        ResponseStatus status = CustomExceptionStatus.INVALID_PARAMETER;
        return ResponseEntity.status(status.getHttpStatus())
                .body(new BaseResponse<>(status));
    }
}
