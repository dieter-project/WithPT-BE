package com.sideproject.withpt.application.notification.service.response.mapper.impl;

import com.sideproject.withpt.application.notification.service.response.NotificationInfoResponse;
import com.sideproject.withpt.application.notification.service.response.mapper.NotificationMapper;
import com.sideproject.withpt.application.pt.service.response.PersonalTrainingResponse;
import com.sideproject.withpt.domain.notification.PersonalTrainingNotification;
import org.springframework.stereotype.Component;

@Component
public class PersonalTrainingNotificationMapper implements NotificationMapper<PersonalTrainingNotification> {

    @Override
    public NotificationInfoResponse<?> toResponse(PersonalTrainingNotification notification) {
        PersonalTrainingResponse personalTrainingResponse = PersonalTrainingResponse.of(notification.getRelatedPersonalTraining());
        return NotificationInfoResponse.from(notification, personalTrainingResponse);
    }
}
