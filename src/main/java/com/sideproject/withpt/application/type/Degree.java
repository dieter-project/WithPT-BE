package com.sideproject.withpt.application.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum Degree {
    HIGH_SCHOOL_DIPLOMA("High School Diploma", "고등학교 졸업장"),
    ASSOCIATE("Associate Degree", "학사"),
    BACHELOR("Bachelor's Degree", "학사"),
    MASTER("Master's Degree", "석사"),
    DOCTORATE("Doctorate Degree", "박사");

    private final String fullName;
    private final String shortName;

    Degree(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }
}
