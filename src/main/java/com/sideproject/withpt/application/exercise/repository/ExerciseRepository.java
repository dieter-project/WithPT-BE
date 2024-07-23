package com.sideproject.withpt.application.exercise.repository;

import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByMemberIdAndUploadDate(Long memberId, LocalDate dateTime);

    Optional<Exercise> findFirstByMemberAndUploadDate(Member member, LocalDate dateTime);
}
