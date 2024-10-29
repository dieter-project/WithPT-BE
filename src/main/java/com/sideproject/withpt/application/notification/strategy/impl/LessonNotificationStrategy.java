package com.sideproject.withpt.application.notification.strategy.impl;

import com.sideproject.withpt.application.notification.strategy.NotificationStrategy;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.notification.LessonNotification;
import com.sideproject.withpt.domain.notification.Notification;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class LessonNotificationStrategy implements NotificationStrategy<Lesson> {

    @Override
    public boolean supports(Object relatedEntity) {
        return relatedEntity instanceof Lesson;
    }

    @Override
    public Notification createNotification(User sender, User receiver, String text, NotificationType type, LocalDateTime createdAt, Lesson relatedEntity) {
        return LessonNotification.builder()
            .sender(sender)
            .receiver(receiver)
            .type(type)
            .text(text)
            .createdAt(createdAt)
            .relatedLesson(relatedEntity)
            .build();
    }
}
