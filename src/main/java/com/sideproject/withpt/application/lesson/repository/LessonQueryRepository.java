package com.sideproject.withpt.application.lesson.repository;

import com.sideproject.withpt.common.type.LessonStatus;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.user.member.Member;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface LessonQueryRepository {

    Optional<Lesson> findByGymTrainerAndDateAndTime(GymTrainer gymTrainer, LocalDate date, LocalTime time);

    List<Lesson> getBookedLessonBy(GymTrainer gymTrainer, LocalDate date);

    List<Lesson> getTrainerLessonScheduleByDate(List<GymTrainer> gymTrainers, LocalDate date);

    List<Lesson> getMemberLessonScheduleByDate(Member member, LocalDate date);

    List<LocalDate> getTrainerLessonScheduleOfMonth(List<GymTrainer> gymTrainers, YearMonth yearMonth);

    List<LocalDate> getMemberLessonScheduleOfMonth(List<GymTrainer> gymTrainers, Member member, YearMonth yearMonth);

    Slice<Lesson> findAllRegisteredByAndLessonStatus(Role role, LessonStatus status, List<GymTrainer> gymTrainers, Pageable pageable);

    Slice<Lesson> findAllModifiedByAndLessonStatus(Role role, LessonStatus status, List<GymTrainer> gymTrainers, Pageable pageable);

}
