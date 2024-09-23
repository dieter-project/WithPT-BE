package com.sideproject.withpt.domain.record.diet.utils;

import java.util.List;
import lombok.Getter;

@Getter
public class DietNutritionalStatistics<T extends NutritionalInfo> {

    private double totalCalories;
    private double totalCarbohydrate;
    private double totalProtein;
    private double totalFat;

    public DietNutritionalStatistics() {
        this.totalCalories = 0;
        this.totalCarbohydrate = 0;
        this.totalProtein = 0;
        this.totalFat = 0;
    }

    public static <T extends NutritionalInfo> DietNutritionalStatistics<T> getStatisticsBy(List<T> NutritionalInfos) {
        return NutritionalInfos.stream().collect(
            DietNutritionalStatistics::new,
            DietNutritionalStatistics::accept,
            DietNutritionalStatistics::combine
        );
    }

    private void accept(NutritionalInfo request) {
        this.totalCalories += request.getCalories();
        this.totalCarbohydrate += request.getCarbohydrate();
        this.totalProtein += request.getProtein();
        this.totalFat += request.getFat();
    }

    private void combine(DietNutritionalStatistics other) {
        this.totalCalories += other.totalCalories;
        this.totalCarbohydrate += other.totalCarbohydrate;
        this.totalProtein += other.totalProtein;
        this.totalFat += other.totalFat;
    }
}

