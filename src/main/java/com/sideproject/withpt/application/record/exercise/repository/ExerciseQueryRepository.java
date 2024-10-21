package com.sideproject.withpt.application.record.exercise.repository;

import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public interface ExerciseQueryRepository {
    Map<LocalDate, Exercise> findExercisesByYearMonth(Member member, int year, int month);
    Optional<ExerciseInfo> findExerciseInfoById(Long id);
}
