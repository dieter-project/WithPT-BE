package com.sideproject.withpt.application.notification.service.response.mapper.impl;

import com.sideproject.withpt.application.lesson.service.response.LessonResponse;
import com.sideproject.withpt.application.notification.service.response.NotificationInfoResponse;
import com.sideproject.withpt.application.notification.service.response.mapper.NotificationMapper;
import com.sideproject.withpt.domain.notification.LessonNotification;
import org.springframework.stereotype.Component;

@Component
public class LessonNotificationMapper implements NotificationMapper<LessonNotification> {

    @Override
    public NotificationInfoResponse<?> toResponse(LessonNotification notification) {
        LessonResponse lessonResponse = LessonResponse.of(notification.getRelatedLesson());
        return NotificationInfoResponse.from(notification, lessonResponse);
    }
}
