package com.sideproject.withpt.application.diet.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.application.type.Usages;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
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
    private List<DietFoodResponse> dietFoods = new ArrayList<>();
    private List<ImageResponse> images;

    @JsonIgnore
    private String imageUsageIdentificationId;

    @QueryProjection
    public DietInfoResponse(Long id, MealCategory mealCategory, LocalDateTime mealTime, double totalCalorie, double totalProtein,
        double totalCarbohydrate, double totalFat, List<DietFoodResponse> dietFoods, String imageUsageIdentificationId) {
        this.id = id;
        this.mealCategory = mealCategory;
        this.mealTime = mealTime;
        this.totalCalorie = totalCalorie;
        this.totalProtein = totalProtein;
        this.totalCarbohydrate = totalCarbohydrate;
        this.totalFat = totalFat;
        this.dietFoods = dietFoods;
        this.imageUsageIdentificationId = imageUsageIdentificationId;
    }

    public void setImages(List<ImageResponse> images) {
        this.images = images;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DietFoodResponse {

        private Long id;
        private String name;
        private int capacity;
        private String units;
        private double calories;
        private double carbohydrate;    // 탄수화물
        private double protein;    // 단백질
        private double fat;    // 지방

        @QueryProjection
        public DietFoodResponse(Long id, String name, int capacity, String units, double calories, double carbohydrate, double protein,
            double fat) {
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

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ImageResponse {

        private Long id;
        private String usageIdentificationId;
        private Usages usages;
        private LocalDate uploadDate;
        private String url;
        private String attachType;

        @QueryProjection
        public ImageResponse(Long id, String usageIdentificationId, Usages usages, LocalDate uploadDate, String url, String attachType) {
            this.id = id;
            this.usageIdentificationId = usageIdentificationId;
            this.usages = usages;
            this.uploadDate = uploadDate;
            this.url = url;
            this.attachType = attachType;
        }
    }
}
