package com.sideproject.withpt.common.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum BodyPart {

    // 전신
    FULL_BODY("전신"),

    // 하체
    LOWER_BODY("하체"),
    GLUTES("엉덩이"),
    QUADRICEPS("앞 허벅지"),
    HAMSTRINGS("뒤 허벅지"),
    CALVES("종아리"),
    ADDUCTORS("내전근"),

    // 상체
    UPPER_BODY("상체"),
    CHEST("가슴"),
    SHOULDERS("어깨"),
    ARMS("팔"),
    ABDOMINALS("복부"),
    LOWER_BACK("허리"),
    BACK("등");

    private final String description;

    BodyPart(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}