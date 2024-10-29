package com.sideproject.withpt.domain.notification;

import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("LESSON")
public class LessonNotification extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson relatedLesson;

    @Builder
    private LessonNotification(NotificationType type, String text, User sender, User receiver, LocalDateTime createdAt, Lesson relatedLesson) {
        super(type, text, sender, receiver, createdAt);
        this.relatedLesson = relatedLesson;
    }
}
