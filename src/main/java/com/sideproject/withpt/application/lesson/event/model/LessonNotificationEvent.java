package com.sideproject.withpt.application.lesson.event.model;

import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LessonNotificationEvent {

    private final User requester;
    private final User receiver;
    private final String message;
    private final NotificationType notificationType;
    private final Lesson lesson;

    @Builder
    private LessonNotificationEvent(User requester, User receiver, String message, NotificationType notificationType, Lesson lesson) {
        this.requester = requester;
        this.receiver = receiver;
        this.message = message;
        this.notificationType = notificationType;
        this.lesson = lesson;
    }

    public static LessonNotificationEvent create(User requester, User receiver, String message, NotificationType notificationType, Lesson lesson) {
        return LessonNotificationEvent.builder()
            .requester(requester)
            .receiver(receiver)
            .message(message)
            .notificationType(notificationType)
            .lesson(lesson)
            .build();
    }
}
