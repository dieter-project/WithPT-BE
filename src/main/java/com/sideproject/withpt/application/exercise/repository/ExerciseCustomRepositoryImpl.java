package com.sideproject.withpt.application.exercise.repository;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExerciseCustomRepositoryImpl implements ExerciseCustomRepository {

    @Override
    public void modifyExercise(Long exerciseId, ExerciseCreateRequest request) {

    }
}
