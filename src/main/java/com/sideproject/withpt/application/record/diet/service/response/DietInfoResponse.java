package com.sideproject.withpt.application.record.diet.service.response;

import com.sideproject.withpt.application.record.diet.repository.response.DietInfoDto;
import com.sideproject.withpt.application.type.MealCategory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietInfoResponse {

    private Long id;
    private MealCategory mealCategory;
    private LocalDateTime mealTime;
    private double totalCalorie;
    private double totalProtein;
    private double totalCarbohydrate;
    private double totalFat;
    private List<DietFoodResponse> dietFoods;
    private List<ImageResponse> images;

    @Builder
    private DietInfoResponse(Long id, MealCategory mealCategory, LocalDateTime mealTime, double totalCalorie, double totalProtein,
        double totalCarbohydrate, double totalFat, List<DietFoodResponse> dietFoods, List<ImageResponse> images) {
        this.id = id;
        this.mealCategory = mealCategory;
        this.mealTime = mealTime;
        this.totalCalorie = totalCalorie;
        this.totalCarbohydrate = totalCarbohydrate;
        this.totalProtein = totalProtein;
        this.totalFat = totalFat;
        this.dietFoods = dietFoods;
        this.images = images;
    }

    public static DietInfoResponse of(DietInfoDto dietInfoDto) {
        return DietInfoResponse.builder()
            .id(dietInfoDto.getId())
            .mealCategory(dietInfoDto.getMealCategory())
            .mealTime(dietInfoDto.getMealTime())
            .totalCalorie(dietInfoDto.getTotalCalorie())
            .totalCarbohydrate(dietInfoDto.getTotalCarbohydrate())
            .totalProtein(dietInfoDto.getTotalProtein())
            .totalFat(dietInfoDto.getTotalFat())
            .dietFoods(dietInfoDto.getDietFoods().stream().map(DietFoodResponse::of).collect(Collectors.toList()))
            .images(dietInfoDto.getImages().stream().map(ImageResponse::of).collect(Collectors.toList()))
            .build();
    }
}
