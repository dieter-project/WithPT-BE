package com.sideproject.withpt.application.record.diet.controller.request;

import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EditDietInfoRequest {

    private LocalDate uploadDate;

    @ValidEnum(enumClass = MealCategory.class)
    private MealCategory mealCategory;

    @NotNull(message = "식사 시간을 입력해주세요.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime mealTime;

    private List<Long> deletedFoodIds;
    private List<Long> deletedImageIds;

    private List<DietFoodRequest> dietFoods;

    @Getter
    public static class Summary {

        private double totalCalories;
        private double totalCarbohydrate;
        private double totalProtein;
        private double totalFat;

        public Summary() {
            this.totalCalories = 0;
            this.totalCarbohydrate = 0;
            this.totalProtein = 0;
            this.totalFat = 0;
        }

        public void accept(DietFoodRequest request) {
            this.totalCalories += request.getCalories();
            this.totalCarbohydrate += request.getCarbohydrate();
            this.totalProtein += request.getProtein();
            this.totalFat += request.getFat();
        }

        public void combine(Summary other) {
            this.totalCalories += other.totalCalories;
            this.totalCarbohydrate += other.totalCarbohydrate;
            this.totalProtein += other.totalProtein;
            this.totalFat += other.totalFat;
        }
    }
}
