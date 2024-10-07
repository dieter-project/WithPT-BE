package com.sideproject.withpt.application.lesson.repository;

import com.sideproject.withpt.application.lesson.repository.dto.MemberLessonInfoResponse;
import com.sideproject.withpt.application.lesson.repository.dto.TrainerLessonInfoResponse;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.Lesson;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface LessonQueryRepository {

    Optional<Lesson> findByGymTrainerAndDateAndTime(GymTrainer gymTrainer, LocalDate date, LocalTime time);

    List<Lesson> getBookedLessonBy(GymTrainer gymTrainer, LocalDate date);

    List<TrainerLessonInfoResponse> getTrainerLessonScheduleByDate(List<GymTrainer> gymTrainers, LocalDate date);

    List<MemberLessonInfoResponse> getMemberLessonScheduleByDate(Member member, LocalDate date);

    List<TrainerLessonInfoResponse> getLessonScheduleMembers(Long trainerId, Long gymId, LocalDate date, LessonStatus status);

    TrainerLessonInfoResponse findLessonScheduleInfoBy(Long lessonId);

    List<LocalDate> getLessonScheduleOfMonth(Long trainerId, Long gymId, YearMonth date);

}
