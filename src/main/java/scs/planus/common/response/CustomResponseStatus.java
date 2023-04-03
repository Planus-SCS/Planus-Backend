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
    EXPIRED_REFRESH_TOKEN(BAD_REQUEST, 2302, "Refresh Token 이 만료되어 재로그인이 필요합니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, 2303, "잘못된 Refresh Token 입니다."),

    // s3 exception
    INVALID_FILE(BAD_REQUEST, 5000, "잘못되거나 존재하지 않는 파일입니다."),
    INVALID_FILE_EXTENSION(BAD_REQUEST, 5001, "잘못된 확장자입니다. jpeg / jpg / png / heic 파일을 선택해주세요.")
    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
