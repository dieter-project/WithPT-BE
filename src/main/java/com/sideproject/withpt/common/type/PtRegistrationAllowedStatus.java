package com.sideproject.withpt.common.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum PtRegistrationAllowedStatus {

    ALLOWED("허용됨"),              // PT 등록이 허용된 상태
    WAITING("대기 중"),            // PT 등록이 대기 중인 상태
    REJECTED("거부됨");            // PT 등록이 거부된 상태


    private final String description;

    PtRegistrationAllowedStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
