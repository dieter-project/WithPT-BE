package com.sideproject.withpt.application.record.diet.controller.request;

import com.sideproject.withpt.domain.record.diet.DietFood;
import com.sideproject.withpt.domain.record.diet.utils.NutritionalInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietFoodRequest implements NutritionalInfo {

    private String name;

    private int capacity;

    private String units;

    private double calories;

    private double carbohydrate;    // 탄수화물

    private double protein;    // 단백질

    private double fat;    // 지방

    public DietFood toEntity() {
        return DietFood.builder()
            .name(name)
            .capacity(capacity)
            .units(units)
            .calories(calories)
            .carbohydrate(carbohydrate)
            .protein(protein)
            .fat(fat)
            .build();
    }

}
