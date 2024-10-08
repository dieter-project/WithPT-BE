package com.sideproject.withpt.application.record.exercise.controller.response;

import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.ExerciseType;
import com.sideproject.withpt.domain.record.exercise.BodyCategory;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "MainExerciseInfoResponse")
public class ExerciseInfoResponse {

    private Long id;
    private LocalDate uploadDate;
    private ExerciseInformation exerciseInfo;

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Schema(name = "MainExerciseInfoResponse.ExerciseInformation")
    public static class ExerciseInformation {

        private Long id;
        private String title;
        private ExerciseType exerciseType;
        private BodyPart bodyPart;
        private List<BodyPart> specificBodyParts;
        private int weight;
        private int exerciseSet;
        private int times;
        private int exerciseTime;

        public static ExerciseInformation of(ExerciseInfo exerciseInfo) {
            BodyPart bodyPart = Optional.ofNullable(exerciseInfo.getBodyCategory())
                .map(BodyCategory::getName)
                .orElse(null);

            List<BodyPart> specificBodyParts = Optional.ofNullable(exerciseInfo.getBodyCategory())
                .map(BodyCategory::getChildren)
                .filter(children -> !children.isEmpty())
                .map(children -> children.stream()
                    .map(BodyCategory::getName)
                    .collect(Collectors.toList()))
                .orElse(null);

            return ExerciseInformation.builder()
                .id(exerciseInfo.getId())
                .title(exerciseInfo.getTitle())
                .exerciseType(exerciseInfo.getExerciseType())
                .bodyPart(bodyPart)
                .specificBodyParts(specificBodyParts)
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
