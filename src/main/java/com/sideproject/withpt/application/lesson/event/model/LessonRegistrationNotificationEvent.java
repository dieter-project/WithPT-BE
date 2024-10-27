package com.sideproject.withpt.application.lesson.event.model;

import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LessonRegistrationNotificationEvent {

    private final User requester;
    private final User receiver;
    private final String message;
    private final NotificationType notificationType;
    private final Long gymId;
    private final LocalDate date;
    private final LocalTime time;

    @Builder
    public LessonRegistrationNotificationEvent(User requester, User receiver, String message, NotificationType notificationType, Long gymId, LocalDate date, LocalTime time) {
        this.requester = requester;
        this.receiver = receiver;
        this.message = message;
        this.notificationType = notificationType;
        this.gymId = gymId;
        this.date = date;
        this.time = time;
    }

    public static LessonRegistrationNotificationEvent create(User requester, User receiver, String message, NotificationType notificationType, Long gymId, LocalDate date, LocalTime time) {
        return LessonRegistrationNotificationEvent.builder()
            .requester(requester)
            .receiver(receiver)
            .message(message)
            .notificationType(notificationType)
            .gymId(gymId)
            .date(date)
            .time(time)
            .build();
    }
}
