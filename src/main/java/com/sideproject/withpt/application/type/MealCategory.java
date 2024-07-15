package com.sideproject.withpt.application.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum MealCategory {

    BREAKFAST("아침"), // 아침
    BRUNCH("아점"),    // 아점
    LUNCH("점심"),     // 점심
    LINNER("점저"),   // 점저
    DINNER("저녁"),    // 저녁
    LNS("야식"),    // 야식
    SNACK("간식")      // 간식;
    ;

    private final String description;

    MealCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
