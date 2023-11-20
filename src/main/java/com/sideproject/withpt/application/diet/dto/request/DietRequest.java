package com.sideproject.withpt.application.diet.dto.request;

import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Diet;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietRequest {

    @ValidEnum(enumClass = MealCategory.class)
    private MealCategory mealCategory;

    @NotNull(message = "식사 시간을 입력해주세요.")
    private LocalDateTime mealTime;

    private List<FoodItemRequest> foodItems;

    public Diet toEntity(Member member) {
        return Diet.builder()
                .member(member)
                .mealCategory(mealCategory)
                .mealTime(mealTime)
                .build();
    }

}
