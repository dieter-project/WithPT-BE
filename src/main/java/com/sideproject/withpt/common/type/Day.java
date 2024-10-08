package com.sideproject.withpt.common.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum Day {
    MON, TUE, WED, THU, FRI, SAT, SUN
}
