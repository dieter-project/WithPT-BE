package com.sideproject.withpt.application.record.diet.repository.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.common.type.DietCategory;
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
    private DietCategory dietCategory;
    private LocalDateTime dietTime;
    private double totalCalorie;
    private double totalProtein;
    private double totalCarbohydrate;
    private double totalFat;
    private List<DietFoodDto> dietFoods;
    private List<ImageDto> images;

    @QueryProjection
    public DietInfoDto(Long id, DietCategory dietCategory, LocalDateTime dietTime, double totalCalorie, double totalProtein,
        double totalCarbohydrate, double totalFat, List<DietFoodDto> dietFoods) {
        this.id = id;
        this.dietCategory = dietCategory;
        this.dietTime = dietTime;
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
