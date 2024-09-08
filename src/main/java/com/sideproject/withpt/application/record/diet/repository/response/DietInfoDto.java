package com.sideproject.withpt.application.record.diet.repository.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.type.MealCategory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietInfoDto {

    private Long id;
    private MealCategory mealCategory;
    private LocalDateTime mealTime;
    private double totalCalorie;
    private double totalProtein;
    private double totalCarbohydrate;
    private double totalFat;
    private List<DietFoodDto> dietFoods;
    private List<ImageDto> images;

    @QueryProjection
    public DietInfoDto(Long id, MealCategory mealCategory, LocalDateTime mealTime, double totalCalorie, double totalProtein,
        double totalCarbohydrate, double totalFat, List<DietFoodDto> dietFoods) {
        this.id = id;
        this.mealCategory = mealCategory;
        this.mealTime = mealTime;
        this.totalCalorie = totalCalorie;
        this.totalProtein = totalProtein;
        this.totalCarbohydrate = totalCarbohydrate;
        this.totalFat = totalFat;
        this.dietFoods = dietFoods;
    }

    public void setImages(List<ImageDto> images) {
        this.images = images;
    }

}
