package com.sideproject.withpt.application.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum ExerciseType {

    AEROBIC("유산소"),    // 유산소
    ANAEROBIC("무산소"),  // 무산소
    STRETCHING("스트레칭"); // 스트레칭

    private final String description;

    ExerciseType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
