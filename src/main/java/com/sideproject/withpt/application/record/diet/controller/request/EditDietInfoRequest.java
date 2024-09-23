package com.sideproject.withpt.application.record.diet.controller.request;

import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class EditDietInfoRequest {

    private LocalDate uploadDate;

    @ValidEnum(enumClass = MealCategory.class)
    private MealCategory mealCategory;

    @NotNull(message = "식사 시간을 입력해주세요.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime mealTime;

    private List<Long> deletedFoodIds;
    private List<Long> deletedImageIds;

    private List<DietFoodRequest> dietFoods;

    public LocalDateTime getDietDateTime() {
        return LocalDateTime.of(uploadDate, mealTime);
    }
}
