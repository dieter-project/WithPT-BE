package com.sideproject.withpt.application.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum AcademicInstitution {
    FOUR_YEAR_UNIVERSITY("대학교(4년제)"),
    THREE_YEAR_COLLEGE("대학교(3년제)"),
    TWO_YEAR_COLLEGE("대학교(2년제)"),
    GRADUATE_SCHOOL("대학원"),
    OVERSEAS_UNIVERSITY("해외대학"),
    HIGH_SCHOOL("고등학교");

    private final String description;

    AcademicInstitution(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
