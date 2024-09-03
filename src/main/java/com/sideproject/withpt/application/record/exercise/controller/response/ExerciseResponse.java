package com.sideproject.withpt.application.record.exercise.controller.response;

import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "MainExerciseResponse")
public class ExerciseResponse {

    private Long id;
    private LocalDate uploadDate;
    private int remainingExerciseCountToTarget;
    private List<ExerciseInfoResponse> exerciseInfos;

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
