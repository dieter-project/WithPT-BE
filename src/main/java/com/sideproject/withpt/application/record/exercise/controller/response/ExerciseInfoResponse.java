package com.sideproject.withpt.application.record.exercise.controller.response;

import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseInfoResponse {

    private Long id;
    private LocalDate uploadDate;
    private ExerciseInformation exerciseInfo;

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class ExerciseInformation {

        private Long id;
        private String title;
        private ExerciseType exerciseType;
        private List<BodyPart> bodyParts = new ArrayList<>();
        private int weight;
        private int exerciseSet;
        private int times;
        private int exerciseTime;

        public static ExerciseInformation of(ExerciseInfo exerciseInfo) {
            return ExerciseInformation.builder()
                .id(exerciseInfo.getId())
                .title(exerciseInfo.getTitle())
                .exerciseType(exerciseInfo.getExerciseType())
                .bodyParts(exerciseInfo.getBodyParts())
                .weight(exerciseInfo.getWeight())
                .exerciseSet(exerciseInfo.getExerciseSet())
                .times(exerciseInfo.getTimes())
                .exerciseTime(exerciseInfo.getExerciseTime())
                .build();
        }
    }

    public static ExerciseInfoResponse from(Exercise exercise) {
        return ExerciseInfoResponse.builder()
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
