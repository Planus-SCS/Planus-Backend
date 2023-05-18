package scs.planus.global.util.validator;

import scs.planus.domain.tag.dto.TagCreateRequestDto;
import scs.planus.global.exception.PlanusException;

import java.time.LocalDate;
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
        long sizeAfterDistinct = updateTags.stream()
                .map(TagCreateRequestDto::getName)
                .distinct()
                .count();

        if (sizeAfterDistinct != (long)updateTags.size()) {
            throw new PlanusException( EXIST_DUPLICATE_TAGS );
        }

    }
}
