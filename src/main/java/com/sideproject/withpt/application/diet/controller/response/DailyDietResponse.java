package com.sideproject.withpt.application.diet.controller.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.type.DietType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
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

    @QueryProjection
    public DailyDietResponse(Long id, LocalDate uploadDate, String feedback, double totalCalorie, double totalProtein, double totalCarbohydrate,
        double totalFat, DietType targetDietType) {
        this.id = id;
        this.uploadDate = uploadDate;
        this.feedback = feedback;
        this.totalCalorie = totalCalorie;
        this.totalProtein = totalProtein;
        this.totalCarbohydrate = totalCarbohydrate;
        this.totalFat = totalFat;
        this.targetDietType = targetDietType;
        this.dietInfos = new ArrayList<>();
    }

    public void setDietInfos(List<DietInfoResponse> dietInfos) {
        this.dietInfos = dietInfos;
    }
}
