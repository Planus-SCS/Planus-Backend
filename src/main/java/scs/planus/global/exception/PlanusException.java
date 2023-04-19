package scs.planus.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import scs.planus.global.common.response.ResponseStatus;

@Getter
@RequiredArgsConstructor
public class PlanusException extends RuntimeException {

    private final ResponseStatus status;
}
