package com.sideproject.withpt.application.diet.dto.request;

import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Diets;
import com.sideproject.withpt.domain.record.FoodItem;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietRequest {

    @ValidEnum(enumClass = ExerciseType.class)
    private MealCategory mealCategory;

    @NotNull(message = "식사 시간을 입력해주세요.")
    private LocalDateTime mealTime;

    private List<FoodItemRequest> foodItems;
    private List<MultipartFile> file;

    public Diets toEntity(Member member) {
        Diets diet = Diets.builder()
                .member(member)
                .mealCategory(mealCategory)
                .mealTime(mealTime)
                .build();

        // 식단 상세 추가하기
        for (FoodItemRequest foodItemRequest : foodItems) {
            FoodItem foodItem = foodItemRequest.toEntity(diet);
            diet.addDietFood(foodItem);
        }

        return diet;
    }

}
