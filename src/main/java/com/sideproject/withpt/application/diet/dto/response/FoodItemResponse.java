package com.sideproject.withpt.application.diet.dto.response;

import com.sideproject.withpt.domain.record.FoodItem;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodItemResponse {

    private String name;
    private int gram;
    private int calories;
    private int carbohydrate;
    private int protein;
    private int fat;

    public static FoodItemResponse from(FoodItem foodItem) {
        return FoodItemResponse.builder()
                .name(foodItem.getFood().getName())
                .gram(foodItem.getGram())
                .calories(foodItem.getCalories())
                .carbohydrate(foodItem.getCarbohydrate())
                .protein(foodItem.getProtein())
                .fat(foodItem.getFat())
                .build();
    }

}
