package scs.planus.domain;

import scs.planus.common.exception.PlanusException;
import scs.planus.common.response.CommonResponseStatus;

public enum Color {
    BLUE, GOLD, PINK, PURPLE, GREEN, NAVY, RED, YELLOW;

    public static Color isValid(String color) throws PlanusException {
        for (Color enumColor : Color.values()) {
            if (enumColor.name().equals(color)) {
                return enumColor;
            }
        }
        throw new PlanusException(CommonResponseStatus.INVALID_PARAMETER);
    }
}
