package com.sideproject.withpt.common.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sideproject.withpt.common.exception.validator.CustomEnumDeserializer;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public enum DietType {
    Carb_Protein_Fat("탄단지"),
    PROTEIN("단백질"),
    DIET("다이어트"),
    KETO("키토");

    final String description;

    DietType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
