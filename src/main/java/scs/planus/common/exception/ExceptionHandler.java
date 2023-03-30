package scs.planus.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import scs.planus.common.response.BaseResponse;
import scs.planus.common.response.CommonResponseStatus;
import scs.planus.common.response.ResponseStatus;

import org.springframework.validation.BindingResult;

import java.util.List;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(PlanusException.class)
    public ResponseEntity<Object> handlePlanusException(PlanusException e) {
        ResponseStatus status = e.getStatus();
        return ResponseEntity.status(status.getHttpStatus())
                .body(new BaseResponse<>(status));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidException() {
        ResponseStatus status = CommonResponseStatus.INVALID_PARAMETER;
        return ResponseEntity.status(status.getHttpStatus())
                .body(new BaseResponse<>(status));
    }
}
