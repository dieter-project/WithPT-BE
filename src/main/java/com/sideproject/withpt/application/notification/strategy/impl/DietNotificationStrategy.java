package com.sideproject.withpt.application.notification.strategy.impl;

import com.sideproject.withpt.application.notification.strategy.NotificationStrategy;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.notification.DietNotification;
import com.sideproject.withpt.domain.notification.Notification;
import com.sideproject.withpt.domain.record.diet.Diets;
import com.sideproject.withpt.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class DietNotificationStrategy implements NotificationStrategy<Diets> {

    @Override
    public boolean supports(Object relatedEntity) {
        return relatedEntity instanceof Diets;
    }

    @Override
    public Notification createNotification(User sender, User receiver, String text, NotificationType type, Diets relatedEntity) {
        return DietNotification.builder()
            .sender(sender)
            .receiver(receiver)
            .text(text)
            .type(type)
            .relatedDiet(relatedEntity)
            .build();
    }
}
