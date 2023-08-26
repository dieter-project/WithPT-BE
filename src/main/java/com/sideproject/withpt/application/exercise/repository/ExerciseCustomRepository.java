package com.sideproject.withpt.application.exercise.repository;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseCreateRequest;

public interface ExerciseCustomRepository {
    void modifyExercise(Long exerciseId, ExerciseCreateRequest request);
}
