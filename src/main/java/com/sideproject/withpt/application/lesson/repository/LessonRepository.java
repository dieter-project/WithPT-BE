package com.sideproject.withpt.application.lesson.repository;

import com.sideproject.withpt.domain.lesson.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long>, LessonQueryRepository {

}
