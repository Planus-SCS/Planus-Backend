package scs.planus.domain.category.entity;

import scs.planus.global.exception.PlanusException;

import java.util.Arrays;

import static scs.planus.global.exception.CustomExceptionStatus.INVALID_CATEGORY_COLOR;

public enum Color {
    BLUE, GOLD, PINK, PURPLE, GREEN, NAVY, RED, YELLOW;

    public static Color of(String color){
        return Arrays.stream(Color.values())
                .filter(enumColor -> enumColor.name().equals(color))
                .findAny()
                .orElseThrow(() -> new PlanusException(INVALID_CATEGORY_COLOR));
    }
}
