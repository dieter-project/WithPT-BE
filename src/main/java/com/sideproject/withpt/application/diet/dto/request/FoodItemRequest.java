package com.sideproject.withpt.application.diet.dto.request;

import com.sideproject.withpt.domain.record.Diet;
import com.sideproject.withpt.domain.record.Food;
import com.sideproject.withpt.domain.record.FoodItem;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodItemRequest {

    private Long id;
    private int gram;

    public FoodItem toEntity(Diet diet, Food food) {
        return FoodItem.builder()
                .food(food)
                .diet(diet)
                .gram(gram)
                .build();
    }

}
