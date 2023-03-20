package scs.planus.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonResponseStatus implements ResponseStatus {

    SUCCESS(HttpStatus.OK, 1000, "요청에 성공하였습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, 2000, "잘못된 요청이 존재합니다."),
    INVALID_URL(HttpStatus.NOT_FOUND, 3000, "잘못된 URL 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5000,"서버 내부 오류입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
