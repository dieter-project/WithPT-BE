package com.sideproject.withpt.application.lesson.event.listener;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.exception.GymTrainerException;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.lesson.event.model.LessonNotificationEvent;
import com.sideproject.withpt.application.lesson.event.model.LessonRegistrationNotificationEvent;
import com.sideproject.withpt.application.lesson.exception.LessonException;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.notification.service.NotificationService;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.user.User;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalTime;
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
public class LessonEventListener {

    private final NotificationService notificationService;

    private final LessonRepository lessonRepository;
    private final GymRepository gymRepository;
    private final GymTrainerRepository gymTrainerRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void lessonRegistrationNotificationLog(LessonRegistrationNotificationEvent event) {
        log.info("[LessonEventListener.lessonRegistrationNotificationLog] 수업 등록 알림 로그 | 요청자 [Role.{}}]", event.getRequester().getRole());
        if (event.getRequester().getRole() == Role.MEMBER) {
            Gym gym = findGym(event);
            Trainer trainer = getTrainer(event.getRequester(), event.getReceiver());
            Lesson lesson = findLesson(gym, trainer, event.getDate(), event.getTime());

            createNotification(event.getRequester(), event.getReceiver(), event.getMessage(), event.getNotificationType(), lesson);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNotificationLog(LessonNotificationEvent event) {
        log.info("[LessonEventListener.saveNotificationLog()]");
        createNotification(event.getRequester(), event.getReceiver(), event.getMessage(), event.getNotificationType(), event.getLesson());
    }

    private Gym findGym(LessonRegistrationNotificationEvent event) {
        return gymRepository.findById(event.getGymId())
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);
    }

    private Lesson findLesson(Gym gym, Trainer trainer, LocalDate date, LocalTime time) {
        GymTrainer gymTrainer = gymTrainerRepository.findByTrainerAndGym(trainer, gym)
            .orElseThrow(() -> GymTrainerException.GYM_TRAINER_NOT_MAPPING);

        return lessonRepository.findByGymTrainerAndDateAndTime(gymTrainer, date, time)
            .orElseThrow(() -> LessonException.LESSON_NOT_FOUND);
    }

    private Trainer getTrainer(User requester, User receiver) {
        return (Trainer) (requester.getRole() == Role.TRAINER ? requester : receiver);
    }

    private void createNotification(User requester, User receiver, String message, NotificationType notificationType, Lesson lesson) {
        notificationService.createNotification(
            requester,
            receiver,
            message,
            notificationType,
            lesson
        );
    }


}
