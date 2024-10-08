package com.sideproject.withpt.common.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum ExerciseFrequency {
    FIRST_TIME("첫 운동"),
    ONCE_TWICE_A_WEEK("주 1~2회"),
    THREE_TIMES_A_WEEK_OR_MORE("주 3회 이상"),
    FIVE_TIMES_A_WEEK_OR_MORE("주 5회 이상"),
    EVERYDAY("매일");

    final String description;

    ExerciseFrequency(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
