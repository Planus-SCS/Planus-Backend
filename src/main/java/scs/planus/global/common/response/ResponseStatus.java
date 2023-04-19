package scs.planus.global.common.response;

import org.springframework.http.HttpStatus;

public interface ResponseStatus {

    HttpStatus getHttpStatus();

    int getCode();

    String getMessage();
}
