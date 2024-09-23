package com.sideproject.withpt.domain.record.diet.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.record.diet.controller.request.DietFoodRequest;
import com.sideproject.withpt.domain.record.diet.DietFood;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DietNutritionalStatisticsTest {

    @DisplayName("DietFood 엔티티 식단 성분 통계 계산")
    @Test
    void calculateDietFoodStatisticsTest() {
        // given
        DietFood dietFood1 = createDietFood("test1`", 1200, 10, 20, 30);
        DietFood dietFood2 = createDietFood("test2`", 2300, 15, 5, 3);

        // when
        DietNutritionalStatistics<DietFood> result = DietNutritionalStatistics.getStatisticsBy(List.of(dietFood1, dietFood2));

        // then
        assertThat(result.getTotalCalories()).isEqualTo(3500.0);
        assertThat(result.getTotalCarbohydrate()).isEqualTo(25.0);
        assertThat(result.getTotalProtein()).isEqualTo(25.0);
        assertThat(result.getTotalFat()).isEqualTo(33.0);
    }

    @DisplayName("DietFoodRequest 식단 성분 통계 계산")
    @Test
    void calculateDietFoodRequestStatisticsTest() {
        // given
        DietFoodRequest dietFoodRequest1 = createaDietFoodRequest("test1`", 1200, 10, 20, 30);
        DietFoodRequest dietFoodRequest2 = createaDietFoodRequest("test2`", 2300, 15, 5, 3);

        // when
        DietNutritionalStatistics<DietFoodRequest> result = DietNutritionalStatistics.getStatisticsBy(List.of(dietFoodRequest1, dietFoodRequest2));

        // then
        assertThat(result.getTotalCalories()).isEqualTo(3500.0);
        assertThat(result.getTotalCarbohydrate()).isEqualTo(25.0);
        assertThat(result.getTotalProtein()).isEqualTo(25.0);
        assertThat(result.getTotalFat()).isEqualTo(33.0);
    }

    private DietFood createDietFood(String name, double calories, double carbohydrate, double protein, double fat) {
        return DietFood.builder()
            .name(name)
            .capacity(100)
            .units("g")
            .calories(calories)
            .carbohydrate(carbohydrate)
            .protein(protein)
            .fat(fat)
            .build();
    }

    private DietFoodRequest createaDietFoodRequest(String name, double calories, double carbohydrate, double protein, double fat) {
        return DietFoodRequest.builder()
            .name(name)
            .capacity(100)
            .units("g")
            .calories(calories)
            .carbohydrate(carbohydrate)
            .protein(protein)
            .fat(fat)
            .build();
    }
}