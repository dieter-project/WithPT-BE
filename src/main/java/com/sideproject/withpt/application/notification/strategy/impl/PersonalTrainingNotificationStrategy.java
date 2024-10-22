package com.sideproject.withpt.application.notification.strategy.impl;

import com.sideproject.withpt.application.notification.strategy.NotificationStrategy;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.notification.Notification;
import com.sideproject.withpt.domain.notification.PersonalTrainingNotification;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class PersonalTrainingNotificationStrategy implements NotificationStrategy<PersonalTraining> {

    @Override
    public boolean supports(Object relatedEntity) {
        return relatedEntity instanceof PersonalTraining;
    }

    @Override
    public Notification createNotification(User sender, User receiver, String text, NotificationType type, PersonalTraining relatedEntity) {
        return PersonalTrainingNotification.builder()
            .sender(sender)
            .receiver(receiver)
            .type(type)
            .text(text)
            .relatedPersonalTraining(relatedEntity)
            .build();
    }
}
