package com.sideproject.withpt.application.record.diet.service.response;

import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietResponse {

    private Long id;
    private LocalDate uploadDate;
    private String feedback;
    private double totalCalorie;
    private double totalProtein;
    private double totalCarbohydrate;
    private double totalFat;
    private DietType targetDietType;

    @Builder
    private DietResponse(Long id, LocalDate uploadDate, String feedback, double totalCalorie, double totalProtein, double totalCarbohydrate, double totalFat, DietType targetDietType) {
        this.id = id;
        this.uploadDate = uploadDate;
        this.feedback = feedback;
        this.totalCalorie = totalCalorie;
        this.totalProtein = totalProtein;
        this.totalCarbohydrate = totalCarbohydrate;
        this.totalFat = totalFat;
        this.targetDietType = targetDietType;
    }

    public static DietResponse of(Diets diets) {
        return DietResponse.builder()
            .id(diets.getId())
            .uploadDate(diets.getUploadDate())
            .feedback(diets.getFeedback())
            .totalCalorie(diets.getTotalCalorie())
            .totalProtein(diets.getTotalProtein())
            .totalCarbohydrate(diets.getTotalCarbohydrate())
            .totalFat(diets.getTotalFat())
            .targetDietType(diets.getTargetDietType())
            .build();
    }
}
