package com.sideproject.withpt.application.exercise.repository;

import com.sideproject.withpt.domain.record.exercise.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
        List<Exercise> findByMemberIdAndExerciseDate(Long memberId, LocalDate dateTime);
}
