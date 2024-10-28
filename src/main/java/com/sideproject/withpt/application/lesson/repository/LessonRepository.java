package com.sideproject.withpt.application.lesson.repository;

import com.sideproject.withpt.domain.lesson.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>, LessonQueryRepository {

    @Modifying
    @Query("delete from Lesson l where l.id = :lessonId")
    void deleteLessonById(@Param("lessonId") Long lessonId);
}
