package com.sideproject.withpt.application.pt.event.model;

import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PersonalTrainingApproveNotificationEvent {

    private final User requester;
    private final User receiver;
    private final String message;
    private final NotificationType notificationType;
    private final PersonalTraining personalTraining;

    @Builder
    private PersonalTrainingApproveNotificationEvent(User requester, User receiver, String message, NotificationType notificationType, PersonalTraining personalTraining) {
        this.requester = requester;
        this.receiver = receiver;
        this.message = message;
        this.notificationType = notificationType;
        this.personalTraining = personalTraining;
    }

    public static PersonalTrainingApproveNotificationEvent create(User requester, User receiver, String message, NotificationType notificationType, PersonalTraining personalTraining) {
        return PersonalTrainingApproveNotificationEvent.builder()
            .requester(requester)
            .receiver(receiver)
            .message(message)
            .notificationType(notificationType)
            .personalTraining(personalTraining)
            .build();
    }
}
