package com.sideproject.withpt.application.exercise.repository;

import com.sideproject.withpt.domain.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

}
