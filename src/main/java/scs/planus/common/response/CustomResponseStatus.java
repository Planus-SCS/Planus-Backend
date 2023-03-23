package scs.planus.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomResponseStatus implements ResponseStatus {

    DUPLICATED_EMAIL(HttpStatus.CONFLICT, 2100, "중복된 이메일이 존재합니다."),
    NONE_USER(HttpStatus.BAD_REQUEST, 2110, "존재하지 않는 회원입니다."),
    NONE_SOCIAL_TYPE(HttpStatus.BAD_REQUEST, 2200, "존재하지 않는 소셜 로그인 타입입니다."),

    // jwt exception
    UNAUTHORIZED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 2300, "인증되지 않은 토큰입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
