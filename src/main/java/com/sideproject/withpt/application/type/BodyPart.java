package com.sideproject.withpt.application.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum BodyPart {
    // 전신, 팔, 복근, 하체, 등, 어깨, 가슴, 허리, 엉덩이, 코어
    WHOLE_BODY, ARM, ABS, LOWER_BODY, BACK, SHOULDER, CHEST, WAIST, HIP, CORE
}