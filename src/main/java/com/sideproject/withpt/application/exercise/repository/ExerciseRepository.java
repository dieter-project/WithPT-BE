package com.sideproject.withpt.application.exercise.repository;

import com.sideproject.withpt.application.exercise.dto.response.ExerciseListResponse;
import com.sideproject.withpt.domain.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
        List<ExerciseListResponse> findByMemberIdAndCreateDate(String memberId, LocalDateTime createDate);
}
