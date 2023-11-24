package com.sideproject.withpt.application.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum Usages {
    EXERCISE, MEAL, BODY
}
