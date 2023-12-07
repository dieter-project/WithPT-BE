package com.sideproject.withpt.application.diet.dto.response;

import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.domain.record.Diet;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietResponse {

    private MealCategory mealCategory;
    private LocalDateTime mealTime;
    private List<String> image;

    private List<FoodItemResponse> foodItemResponse;

    public static DietResponse from(Diet diet, List<String> urls, List<FoodItemResponse> foodItem) {
        return DietResponse.builder()
                .mealCategory(diet.getMealCategory())
                .mealTime(diet.getMealTime())
                .image(urls)
                .foodItemResponse(foodItem)
                .build();
    }

}
