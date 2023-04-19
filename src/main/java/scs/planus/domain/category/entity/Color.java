package scs.planus.domain.category.entity;

import scs.planus.global.exception.PlanusException;

import static scs.planus.global.exception.CustomExceptionStatus.INVALID_PARAMETER;

public enum Color {
    BLUE, GOLD, PINK, PURPLE, GREEN, NAVY, RED, YELLOW;

    public static Color isValid(String color) throws PlanusException {
        for (Color enumColor : Color.values()) {
            if (enumColor.name().equals(color)) {
                return enumColor;
            }
        }
        throw new PlanusException(INVALID_PARAMETER);
    }
}
