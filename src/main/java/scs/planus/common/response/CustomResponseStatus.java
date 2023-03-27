package scs.planus.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum CustomResponseStatus implements ResponseStatus {

    DUPLICATED_EMAIL(CONFLICT, 2100, "중복된 이메일이 존재합니다."),
    NONE_USER(BAD_REQUEST, 2110, "존재하지 않는 회원입니다."),
    NONE_SOCIAL_TYPE(BAD_REQUEST, 2200, "존재하지 않는 소셜 로그인 타입입니다."),

    // jwt exception
    UNAUTHORIZED_ACCESS_TOKEN(UNAUTHORIZED, 2300, "인증되지 않거나 만료된 토큰입니다."),
    FORBIDDEN_ACCESS_TOKEN(FORBIDDEN, 2301, "권한이 없는 토큰입니다."),

    // Category exception
    NOT_EXIST_CATEGORY(HttpStatus.BAD_REQUEST, 2400, "존재하지 않는 카테고리 입니다."),

    // Todo exception
    NOT_EXIST_TODO(HttpStatus.BAD_REQUEST, 2500, "존재하지 않는 투두 입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
