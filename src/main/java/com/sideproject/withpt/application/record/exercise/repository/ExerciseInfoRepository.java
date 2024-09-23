package com.sideproject.withpt.application.record.exercise.repository;

import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseInfoRepository extends JpaRepository<ExerciseInfo, Long> {

    @Modifying // 추가
    @Query("delete from ExerciseInfo ei where ei.id = :id")
    void deleteExerciseInfoById(@Param("id") Long id);
}
