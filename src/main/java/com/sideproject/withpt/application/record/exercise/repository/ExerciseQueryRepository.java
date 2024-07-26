package com.sideproject.withpt.application.record.exercise.repository;

import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import java.time.LocalDate;
import java.util.Map;

public interface ExerciseQueryRepository {
    Map<LocalDate, Exercise> findExercisesByYearMonth(Member member, int year, int month);
}
