package com.sideproject.withpt.application.lesson.repository;

import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.domain.pt.Lesson;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long>, LessonQueryRepository {

    List<Lesson> findAllByScheduleDateAndScheduleTimeAndStatus(LocalDate date, LocalTime time, LessonStatus status);

    List<Lesson> findAllByTrainerIdAndStatus(Long trainerId, LessonStatus status);
}
