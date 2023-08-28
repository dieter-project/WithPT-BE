package com.sideproject.withpt.application.exercise.dto.response;

import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.record.Exercise;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseListResponse {

    private String title;
    private int weight;
    private int set;
    private int times;
    private int hour;
    private String bookmarkYn;

    private BodyPart bodyPart;
    private ExerciseType exerciseType;

    public static ExerciseListResponse from(Exercise exercise) {
        return ExerciseListResponse.builder()
                .title(exercise.getTitle())
                .weight(exercise.getWeight())
                .set(exercise.getSet())
                .times(exercise.getTimes())
                .hour(exercise.getHour())
                .bookmarkYn(exercise.getBookmarkYn())
                .bodyPart(exercise.getBodyPart())
                .exerciseType(exercise.getExerciseType())
                .build();
    }

}
