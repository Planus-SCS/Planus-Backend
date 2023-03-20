package scs.planus.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import static scs.planus.common.response.CommonResponseStatus.SUCCESS;

@Getter
public class BaseResponse<T> {

    private final Boolean isSuccess;
    private final int code;
    private final String message;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private T data;

    // 성공시 반환되는 Response
    public BaseResponse(T data) {
        this.isSuccess = true;
        this.code = SUCCESS.getCode();
        this.message = SUCCESS.getMessage();
        this.data = data;
    }

    // 실패시 반환되는 Response
    public BaseResponse(ResponseStatus status) {
        this.isSuccess = false;
        this.code = status.getCode();
        this.message = status.getMessage();
    }
}
