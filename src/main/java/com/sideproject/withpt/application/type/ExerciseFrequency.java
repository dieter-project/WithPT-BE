package com.sideproject.withpt.application.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum ExerciseFrequency {
    FIRST_TIME,
    ONCE_TWICE_A_WEEK,
    THREE_TIMES_A_WEEK_OR_MORE,
    FIVE_TIMES_A_WEEK_OR_MORE,
    EVERYDAY
}
