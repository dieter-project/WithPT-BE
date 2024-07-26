package com.sideproject.withpt.application.record.exercise.controller.request;

import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseEditRequest {

    @NotBlank(message = "운동명을 입력해주세요.")
    private String title;

    @ValidEnum(enumClass = ExerciseType.class)
    private ExerciseType exerciseType;

    private List<String> bodyParts;

    private int weight; // 무게(kg)
    private int times; // 횟수
    private int exerciseSet; // 운동 set

    private int exerciseTime; // 유산소, 스트레칭
}
