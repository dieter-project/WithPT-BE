package com.sideproject.withpt.application.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum MealCategory {

    BREAKFAST, // 아침
    BRUNCH,    // 아점
    LUNCH,     // 점심
    LINNER,   // 점저
    DINNER,    // 저녁
    LNS,    // 야식
    SNACK      // 간식

}
