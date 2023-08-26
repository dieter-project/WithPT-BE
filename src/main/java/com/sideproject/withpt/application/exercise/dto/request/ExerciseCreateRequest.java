package com.sideproject.withpt.application.exercise.dto.request;

import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.record.Exercise;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseCreateRequest {

    @NotBlank(message = "운동명을 입력해주세요.")
    private String title;

    private int weight;
    private int set;
    private int time;

    private String bookmarkYn;

    @ValidEnum(enumClass = BodyPart.class)
    private BodyPart bodyPart;

    @ValidEnum(enumClass = ExerciseType.class)
    private ExerciseType exerciseType;

    public Exercise toEntity() {
        return Exercise.builder()
                .title(title)
                .weight(weight)
                .set(set)
                .time(time)
                .bodyPart(bodyPart)
                .exerciseType(exerciseType)
                .build();
    }

}
