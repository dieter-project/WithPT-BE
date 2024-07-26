package com.sideproject.withpt.application.record.exercise.controller.response;

import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseResponse2 {

    private Long id;
    private String title;
    private int weight;
    private int set;
    private int times;
    private int hour;

    private BodyPart bodyPart;
    private ExerciseType exerciseType;

    public static ExerciseResponse2 from(Exercise exercise) {
        return ExerciseResponse2.builder()
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
