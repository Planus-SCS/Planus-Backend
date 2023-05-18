package scs.planus.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import scs.planus.global.common.response.ResponseStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum CustomExceptionStatus implements ResponseStatus {

    // common exception
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, 2000, "잘못된 요청이 존재합니다."),
    INVALID_URL(HttpStatus.NOT_FOUND, 3000, "잘못된 URL 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5000,"서버 내부 오류입니다."),

    // user exception
    DUPLICATED_EMAIL(CONFLICT, 2100, "중복된 이메일이 존재합니다."),
    NONE_USER(BAD_REQUEST, 2110, "존재하지 않는 회원입니다."),
    NONE_SOCIAL_TYPE(BAD_REQUEST, 2200, "존재하지 않는 소셜 로그인 타입입니다."),

    // jwt exception
    UNAUTHORIZED_ACCESS_TOKEN(UNAUTHORIZED, 2300, "인증되지 않거나 만료된 토큰입니다."),
    FORBIDDEN_ACCESS_TOKEN(FORBIDDEN, 2301, "권한이 없는 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(BAD_REQUEST, 2302, "Refresh Token 이 만료되어 재로그인이 필요합니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, 2303, "잘못된 Refresh Token 입니다."),

    // category exception
    NOT_EXIST_CATEGORY(BAD_REQUEST, 2400, "존재하지 않는 카테고리 입니다."),
    INVALID_CATEGORY_COLOR(BAD_REQUEST, 2401, "소문자가 섞여 있거나, 존재하지 않는 카테고리 색 입니다."),

    // to_do exception
    INVALID_DATE(BAD_REQUEST, 2500, "시작 날짜가 끝 날짜보다 늦을 수 없습니다."),
    NONE_TODO(BAD_REQUEST, 2501, "존재하지 않는 투두입니다."),

    // group exception
    NOT_EXIST_GROUP(BAD_REQUEST, 2600, "존재하지 않는 그룹 입니다."),
    NOT_EXIST_LEADER(BAD_REQUEST, 2601, "해당 그룹에 그룹장이 존재하지 않습니다."),
    NOT_GROUP_LEADER_PERMISSION(BAD_REQUEST, 2602, "그룹을 수정할 권한이 없습니다."),
    EXCEED_GROUP_LIMIT_COUNT(BAD_REQUEST, 2603, "그룹 제한 인원을 초과하였습니다."),
    ALREADY_JOINED_GROUP(BAD_REQUEST, 2604, "이미 가입된 그룹입니다."),
    NOT_EXIST_GROUP_JOIN(BAD_REQUEST, 2605, "존재 하지 않는 그룹 가입 신청서 입니다."),
    DO_NOT_HAVE_TODO_AUTHORITY(BAD_REQUEST, 2606, "그룹 투두 권한이 없습니다."),

    // groupMember excepion
    NOT_JOINED_GROUP(BAD_REQUEST, 2700, "가입하지 않은 그룹 입니다."),
    NOT_JOINED_MEMBER_IN_GROUP(BAD_REQUEST, 2701, "그룹에 가입되어 있지 않은 회원입니다."),

    // tag
    EXIST_DUPLICATE_TAGS(BAD_REQUEST, 2800, "중복된 태그들이 존재합니다."),

    // s3 exception
    INVALID_FILE(BAD_REQUEST, 5000, "잘못되거나 존재하지 않는 파일입니다."),
    INVALID_FILE_EXTENSION(BAD_REQUEST, 5001, "잘못된 확장자입니다. jpeg / jpg / png / heic 파일을 선택해주세요.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
