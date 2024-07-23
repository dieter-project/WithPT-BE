package com.sideproject.withpt.application.exercise.dto.response;

import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseResponse {

    private Long id;
    private String title;
    private int weight;
    private int set;
    private int times;
    private int hour;

    private BodyPart bodyPart;
    private ExerciseType exerciseType;

    public static ExerciseResponse from(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
//                .title(exercise.getTitle())
//                .weight(exercise.getWeight())
//                .set(exercise.getExerciseSet())
//                .times(exercise.getTimes())
//                .hour(exercise.getExerciseTime())
//                .bodyPart(exercise.getBodyPart())
//                .exerciseType(exercise.getExerciseType())
                .build();
    }

}
