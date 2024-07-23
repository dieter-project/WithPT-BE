package com.sideproject.withpt.application.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum BodyPart {

    FULL_BODY("전신"),    // 전신
    ARM("팔"),            // 팔
    ABS("복근"),          // 복근
    LOWER_BODY("하체"),   // 하체
    BACK("등"),           // 등
    SHOULDER("어깨"),     // 어깨
    CHEST("가슴"),        // 가슴
    WAIST("허리"),        // 허리
    BUTTOCKS("엉덩이"),   // 엉덩이
    CORE("코어");         // 코어

    private final String description;

    BodyPart(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}