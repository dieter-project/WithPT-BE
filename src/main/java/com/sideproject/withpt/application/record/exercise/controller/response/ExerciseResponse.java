package com.sideproject.withpt.application.record.exercise.controller.response;

import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "MainExerciseResponse")
public class ExerciseResponse {

    private Long id;
    private LocalDate uploadDate;
    private int remainingExerciseCountToTarget;
    private List<ExerciseInfoResponse> exerciseInfos;

    @Builder
    private ExerciseResponse(Long id, LocalDate uploadDate, int remainingExerciseCountToTarget, List<ExerciseInfoResponse> exerciseInfos) {
        this.id = id;
        this.uploadDate = uploadDate;
        this.remainingExerciseCountToTarget = remainingExerciseCountToTarget;
        this.exerciseInfos = exerciseInfos;
    }

    public static ExerciseResponse of(Exercise exercise) {
        return ExerciseResponse.builder()
            .id(exercise.getId())
            .uploadDate(exercise.getUploadDate())
            .remainingExerciseCountToTarget(0)
            .exerciseInfos(exercise.getExerciseInfos().stream()
                .map(ExerciseInfoResponse::of)
                .collect(Collectors.toList()))
            .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Schema(name = "MainExerciseResponse.ExerciseInfoResponse")
    public static class ExerciseInfoResponse {

        private Long id;
        private String title;
        private ExerciseType exerciseType;
        private List<BodyPart> bodyParts = new ArrayList<>();
        private int weight;
        private int exerciseSet;
        private int times;
        private int exerciseTime;

        public static ExerciseInfoResponse of(ExerciseInfo exerciseInfo) {
            return ExerciseInfoResponse.builder()
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

}
