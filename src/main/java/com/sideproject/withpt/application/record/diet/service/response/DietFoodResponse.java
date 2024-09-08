package com.sideproject.withpt.application.record.diet.service.response;

import com.sideproject.withpt.application.record.diet.repository.response.DietFoodDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietFoodResponse {

    private Long id;
    private String name;
    private int capacity;
    private String units;
    private double calories;
    private double carbohydrate;    // 탄수화물
    private double protein;    // 단백질
    private double fat;    // 지방

    @Builder
    private DietFoodResponse(Long id, String name, int capacity, String units, double calories, double carbohydrate, double protein, double fat) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.units = units;
        this.calories = calories;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
    }

    public static DietFoodResponse of(DietFoodDto dietFoodDto) {
        return DietFoodResponse.builder()
            .id(dietFoodDto.getId())
            .name(dietFoodDto.getName())
            .capacity(dietFoodDto.getCapacity())
            .units(dietFoodDto.getUnits())
            .calories(dietFoodDto.getCalories())
            .carbohydrate(dietFoodDto.getCarbohydrate())
            .protein(dietFoodDto.getProtein())
            .fat(dietFoodDto.getFat())
            .build();
    }
}
