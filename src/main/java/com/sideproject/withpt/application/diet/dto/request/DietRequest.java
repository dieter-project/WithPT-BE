package com.sideproject.withpt.application.diet.dto.request;

import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.record.Food;
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

    private List<Food> foods;
    private List<MultipartFile> file;

}
