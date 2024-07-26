package com.sideproject.withpt.application.record.exercise.repository;

import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseInfoRepository extends JpaRepository<ExerciseInfo, Long> {

}
