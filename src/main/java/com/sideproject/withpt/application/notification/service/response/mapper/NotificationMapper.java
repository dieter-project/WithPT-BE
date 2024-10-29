package com.sideproject.withpt.application.notification.service.response.mapper;

import com.sideproject.withpt.application.notification.service.response.NotificationInfoResponse;
import com.sideproject.withpt.domain.notification.Notification;

public interface NotificationMapper<T extends Notification> {

    NotificationInfoResponse<?> toResponse(T notification);
}
