package scs.planus.global.util.validator;

import scs.planus.global.exception.PlanusException;

import java.time.LocalDate;

import static scs.planus.global.exception.CustomExceptionStatus.INVALID_DATE;

public class Validator {

    public static void validateStartDateBeforeEndDate(LocalDate startDate, LocalDate endDate) {
        if (endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new PlanusException(INVALID_DATE);
            }
        }
    }
}
