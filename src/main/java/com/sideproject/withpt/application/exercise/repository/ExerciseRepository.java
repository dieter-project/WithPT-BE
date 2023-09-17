package com.sideproject.withpt.application.exercise.repository;

import com.sideproject.withpt.domain.record.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
        List<Exercise> findByMemberIdAndExerciseDateBetween(Long memberId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
