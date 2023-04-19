package scs.planus.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class BaseResponse<T> {

    private static final int SUCCESS_CODE = 1000;
    private static final String SUCCESS_MESSAGE = "요청에 성공하였습니다.";

    private final Boolean isSuccess;
    private final int code;
    private final String message;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private T data;

    // 성공시 반환되는 Response
    public BaseResponse(T data) {
        this.isSuccess = true;
        this.code = SUCCESS_CODE;
        this.message = SUCCESS_MESSAGE;
        this.data = data;
    }

    // 실패시 반환되는 Response
    public BaseResponse(ResponseStatus status) {
        this.isSuccess = false;
        this.code = status.getCode();
        this.message = status.getMessage();
    }
}
