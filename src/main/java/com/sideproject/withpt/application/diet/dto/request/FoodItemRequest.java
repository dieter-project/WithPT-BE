package com.sideproject.withpt.application.diet.dto.request;

import com.sideproject.withpt.domain.record.Diets;
import com.sideproject.withpt.domain.record.Food;
import com.sideproject.withpt.domain.record.FoodItem;
import lombok.*;

import javax.validation.constraints.Min;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodItemRequest {

    private Food food;

    @Min(value = 0, message = "식사량을 0 이상 입력해주세요.")
    private int gram;

    public FoodItem toEntity(Diets diets) {
        return FoodItem.builder()
                .food(food)
                .diets(diets)
                .gram(gram)
                .build();
    }

}
