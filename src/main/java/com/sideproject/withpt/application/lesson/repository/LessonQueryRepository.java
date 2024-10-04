package com.sideproject.withpt.application.lesson.repository;

import com.sideproject.withpt.application.lesson.repository.dto.LessonInfoResponse;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.pt.Lesson;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LessonQueryRepository {

    Optional<Lesson> findByGymTrainerAndDateAndTime(GymTrainer gymTrainer, LocalDate date, LocalTime time);
    Map<LocalTime, Boolean> getAvailableTrainerLessonSchedule(Long trainerId, Gym gym, Day weekday, LocalDate date);
    List<LessonInfoResponse> getLessonScheduleMembers(Long trainerId, Long gymId, LocalDate date, LessonStatus status);
    LessonInfoResponse findLessonScheduleInfoBy(Long lessonId);
    List<LocalDate> getLessonScheduleOfMonth(Long trainerId, Long gymId, YearMonth date);

}
