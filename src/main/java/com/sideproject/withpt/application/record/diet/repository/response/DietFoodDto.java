package com.sideproject.withpt.application.record.diet.repository.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietFoodDto {

    private Long id;
    private String name;
    private int capacity;
    private String units;
    private double calories;
    private double carbohydrate;    // 탄수화물
    private double protein;    // 단백질
    private double fat;    // 지방

    @QueryProjection
    public DietFoodDto(Long id, String name, int capacity, String units, double calories, double carbohydrate, double protein, double fat) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.units = units;
        this.calories = calories;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
    }
}
