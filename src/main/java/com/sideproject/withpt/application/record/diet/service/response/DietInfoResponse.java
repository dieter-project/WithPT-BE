package com.sideproject.withpt.application.record.diet.service.response;

import com.sideproject.withpt.application.record.diet.repository.response.DietInfoDto;
import com.sideproject.withpt.common.type.DietCategory;
import com.sideproject.withpt.domain.record.Image;
import com.sideproject.withpt.domain.record.diet.DietFood;
import com.sideproject.withpt.domain.record.diet.DietInfo;
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
    private DietCategory dietCategory;
    private LocalDateTime dietTime;
    private double totalCalorie;
    private double totalProtein;
    private double totalCarbohydrate;
    private double totalFat;
    private List<DietFoodResponse> dietFoods;
    private List<ImageResponse> images;

    @Builder
    private DietInfoResponse(Long id, DietCategory dietCategory, LocalDateTime dietTime, double totalCalorie, double totalProtein,
        double totalCarbohydrate, double totalFat, List<DietFoodResponse> dietFoods, List<ImageResponse> images) {
        this.id = id;
        this.dietCategory = dietCategory;
        this.dietTime = dietTime;
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
            .dietCategory(dietInfoDto.getDietCategory())
            .dietTime(dietInfoDto.getDietTime())
            .totalCalorie(dietInfoDto.getTotalCalorie())
            .totalCarbohydrate(dietInfoDto.getTotalCarbohydrate())
            .totalProtein(dietInfoDto.getTotalProtein())
            .totalFat(dietInfoDto.getTotalFat())
            .dietFoods(dietInfoDto.getDietFoods().stream().map(DietFoodResponse::of).collect(Collectors.toList()))
            .images(dietInfoDto.getImages().stream().map(ImageResponse::of).collect(Collectors.toList()))
            .build();
    }

    public static DietInfoResponse of(DietInfo dietInfo, List<DietFood> dietFoods, List<Image> images) {
        return DietInfoResponse.builder()
            .id(dietInfo.getId())
            .dietCategory(dietInfo.getDietCategory())
            .dietTime(dietInfo.getDietTime())
            .totalCalorie(dietInfo.getTotalCalorie())
            .totalCarbohydrate(dietInfo.getTotalCarbohydrate())
            .totalProtein(dietInfo.getTotalProtein())
            .totalFat(dietInfo.getTotalFat())
            .dietFoods(
                dietFoods.stream()
                    .map(DietFoodResponse::of)
                    .collect(Collectors.toList())
            )
            .images(
                images.stream()
                    .map(ImageResponse::of)
                    .collect(Collectors.toList())
            )
            .build();
    }
}
