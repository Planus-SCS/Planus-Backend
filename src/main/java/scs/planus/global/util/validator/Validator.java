package scs.planus.global.util.validator;

import scs.planus.domain.tag.dto.TagCreateRequestDto;
import scs.planus.global.exception.PlanusException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static scs.planus.global.exception.CustomExceptionStatus.EXIST_DUPLICATE_TAGS;
import static scs.planus.global.exception.CustomExceptionStatus.INVALID_DATE;

public class Validator {

    public static void validateStartDateBeforeEndDate(LocalDate startDate, LocalDate endDate) {
        if (endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new PlanusException(INVALID_DATE);
            }
        }
    }


    public static void validateDuplicateTagName(List<TagCreateRequestDto> updateTags) {
        // HashSet 의 add()를 통해 중복된 값이 있으면 false
        boolean isDuplcate = !updateTags.stream()
                .map( TagCreateRequestDto::getName )
                .allMatch( new HashSet<>()::add );

        if (isDuplcate) {
            throw new PlanusException( EXIST_DUPLICATE_TAGS );
        }
    }
}
