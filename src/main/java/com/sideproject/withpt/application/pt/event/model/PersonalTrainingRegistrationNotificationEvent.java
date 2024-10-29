package com.sideproject.withpt.application.pt.event.model;

import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.user.User;
import com.sideproject.withpt.domain.user.member.Member;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PersonalTrainingRegistrationNotificationEvent {

    private final User requester;
    private final User receiver;
    private final String message;
    private final NotificationType notificationType;
    private final LocalDateTime createdAt;
    private final Member member;
    private final GymTrainer gymTrainer;

    @Builder
    private PersonalTrainingRegistrationNotificationEvent(User requester, User receiver, String message, NotificationType notificationType, LocalDateTime createdAt, Member member, GymTrainer gymTrainer) {
        this.requester = requester;
        this.receiver = receiver;
        this.message = message;
        this.notificationType = notificationType;
        this.createdAt = createdAt;
        this.member = member;
        this.gymTrainer = gymTrainer;
    }

    public static PersonalTrainingRegistrationNotificationEvent create(User requester, User receiver, String message, NotificationType notificationType, LocalDateTime createdAt, Member member, GymTrainer gymTrainer) {
        return PersonalTrainingRegistrationNotificationEvent.builder()
            .requester(requester)
            .receiver(receiver)
            .message(message)
            .notificationType(notificationType)
            .createdAt(createdAt)
            .member(member)
            .gymTrainer(gymTrainer)
            .build();
    }
}
