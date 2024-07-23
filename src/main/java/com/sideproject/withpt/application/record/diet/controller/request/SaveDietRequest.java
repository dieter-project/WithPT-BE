package com.sideproject.withpt.application.record.diet.controller.request;

import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.diet.Diets;
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
public class SaveDietRequest {

    private LocalDate uploadDate;

    @ValidEnum(enumClass = MealCategory.class)
    private MealCategory mealCategory;

    @NotNull(message = "식사 시간을 입력해주세요.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime mealTime;

    private List<DietFoodRequest> dietFoods;

    public Diets toEntity(Member member) {

        // 초기값을 0으로 설정한 Summary 객체 생성
        Summary summary = dietFoods.stream().collect(
            Summary::new,
            Summary::accept,
            Summary::combine
        );

        return Diets.builder()
            .member(member)
            .uploadDate(uploadDate)
            .targetDietType(member.getDietType())
            .totalCalorie(summary.getTotalCalories())
            .totalCarbohydrate(summary.getTotalCarbohydrate())
            .totalProtein(summary.getTotalProtein())
            .totalFat(summary.getTotalFat())
            .build();

    }

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
