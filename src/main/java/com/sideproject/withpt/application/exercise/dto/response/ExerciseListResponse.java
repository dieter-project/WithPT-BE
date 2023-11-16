package com.sideproject.withpt.application.exercise.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseListResponse {

    private List<ExerciseResponse> exercise;
    private List<String> urls;

    public static ExerciseListResponse from(List<ExerciseResponse> exercise, List<String> urls) {
        return ExerciseListResponse.builder()
                .exercise(exercise)
                .urls(urls)
                .build();
    }

}
