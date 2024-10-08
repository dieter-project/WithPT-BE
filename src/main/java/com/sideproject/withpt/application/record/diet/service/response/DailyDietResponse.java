package com.sideproject.withpt.application.record.diet.service.response;

import com.sideproject.withpt.application.record.diet.repository.response.DietInfoDto;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
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
public class DailyDietResponse {

    private Long id;
    private LocalDate uploadDate;
    private String feedback;
    private double totalCalorie;
    private double totalProtein;
    private double totalCarbohydrate;
    private double totalFat;
    private DietType targetDietType;
    private List<DietInfoResponse> dietInfos;

    @Builder
    private DailyDietResponse(Long id, LocalDate uploadDate, String feedback, double totalCalorie, double totalProtein, double totalCarbohydrate,
        double totalFat, DietType targetDietType, List<DietInfoResponse> dietInfos) {
        this.id = id;
        this.uploadDate = uploadDate;
        this.feedback = feedback;
        this.totalCalorie = totalCalorie;
        this.totalProtein = totalProtein;
        this.totalCarbohydrate = totalCarbohydrate;
        this.totalFat = totalFat;
        this.targetDietType = targetDietType;
        this.dietInfos = dietInfos;
    }

    public static DailyDietResponse of(Diets diets, List<DietInfoDto> dietInfoDtos) {
        return DailyDietResponse.builder()
            .id(diets.getId())
            .uploadDate(diets.getUploadDate())
            .feedback(diets.getFeedback())
            .totalCalorie(diets.getTotalCalorie())
            .totalProtein(diets.getTotalProtein())
            .totalCarbohydrate(diets.getTotalCarbohydrate())
            .totalFat(diets.getTotalFat())
            .targetDietType(diets.getTargetDietType())
            .dietInfos(dietInfoDtos.stream()
                .map(DietInfoResponse::of)
                .collect(Collectors.toList()))
            .build();
    }

    public void setDietInfos(List<DietInfoResponse> dietInfos) {
        this.dietInfos = dietInfos;
    }
}
