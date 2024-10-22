package com.sideproject.withpt.application.notification.strategy;

import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.notification.Notification;
import com.sideproject.withpt.domain.user.User;

public interface NotificationStrategy<T> {

    boolean supports(Object relatedEntity);

    Notification createNotification(User sender, User receiver, String text, NotificationType type, T relatedEntity);
}
