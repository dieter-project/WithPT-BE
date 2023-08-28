package com.sideproject.withpt.application.exercise.dto.response;

import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.record.Exercise;
import lombok.*;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseListResponse {

    private String title;
    private int weight;
    private int set;
    private int times;
    private int hour;

    @ValidEnum(enumClass = BodyPart.class)
    private BodyPart bodyPart;

    public static ExerciseListResponse from(Exercise exercise) {
        return ExerciseListResponse.builder()
                .title(exercise.getTitle())
                .weight(exercise.getWeight())
                .set(exercise.getSet())
                .times(exercise.getTimes())
                .hour(exercise.getHour())
                .bodyPart(exercise.getBodyPart())
                .build();
    }

}
