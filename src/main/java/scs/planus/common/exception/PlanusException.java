package scs.planus.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import scs.planus.common.response.ResponseStatus;

@Getter
@RequiredArgsConstructor
public class PlanusException extends RuntimeException {

    private final ResponseStatus status;
}
