package com.sideproject.withpt.application.pt.event.listener;

import com.sideproject.withpt.application.notification.service.NotificationService;
import com.sideproject.withpt.application.pt.event.model.PersonalTrainingApproveNotificationEvent;
import com.sideproject.withpt.application.pt.event.model.PersonalTrainingRegistrationNotificationEvent;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.user.User;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonalTrainingEventListener {

    private final NotificationService notificationService;
    private final PersonalTrainingRepository personalTrainingRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void personalTrainingRegistrationNotification(PersonalTrainingRegistrationNotificationEvent event) {
        log.info("[PersonalTrainingEventListener.personalTrainingRegistrationNotification]");

        PersonalTraining personalTraining = findPersonalTraining(event.getMember(), event.getGymTrainer());
        createNotification(event.getRequester(), event.getReceiver(), event.getMessage(), event.getNotificationType(), personalTraining);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void approvedPersonalTrainingRegistrationNotification(PersonalTrainingApproveNotificationEvent event) {
        log.info("[PersonalTrainingEventListener.approvedPersonalTrainingRegistrationNotification()]");

        PersonalTraining personalTraining = event.getPersonalTraining();
        Trainer receiver = personalTraining.getGymTrainer().getTrainer();
        createNotification(event.getRequester(), receiver, event.getMessage(), event.getNotificationType(), personalTraining);
    }

    private PersonalTraining findPersonalTraining(Member member, GymTrainer gymTrainer) {
        return personalTrainingRepository.findByMemberAndGymTrainer(member, gymTrainer)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);
    }

    private void createNotification(User requester, User receiver, String message, NotificationType notificationType, PersonalTraining personalTraining) {
        notificationService.createNotification(
            requester,
            receiver,
            message,
            notificationType,
            personalTraining
        );
    }
}
