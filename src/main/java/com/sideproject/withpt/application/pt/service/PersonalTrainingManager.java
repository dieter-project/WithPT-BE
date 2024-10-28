package com.sideproject.withpt.application.pt.service;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.exception.GymTrainerException;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.pt.event.model.PersonalTrainingApproveNotificationEvent;
import com.sideproject.withpt.application.pt.event.model.PersonalTrainingRegistrationNotificationEvent;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.pt.service.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.user.UserRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.user.User;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalTrainingManager {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final GymTrainerRepository gymTrainerRepository;
    private final PersonalTrainingRepository personalTrainingRepository;

    private final PersonalTrainingService personalTrainingService;

    private final ApplicationEventPublisher eventPublisher;

    private static final String PT_REGISTRATION_REQUEST_MSG = "%s 트레이너/%s 피트니스 PT 등록 요청이 도착했습니다.";
    private static final String PT_REGISTRATION_ACCEPTED_MSG = "%s 회원님이 PT 등록을 수락하였습니다.";

    @Transactional
    public PersonalTrainingMemberResponse registerPersonalTraining(Long gymId, Long memberId, Long trainerId, LocalDateTime ptRegistrationRequestDate) {
        User requester = findUserById(trainerId);
        User receiver = findUserById(memberId);
        Gym gym = findGymById(gymId);

        GymTrainer gymTrainer = getGymTrainerBy(gym, (Trainer) requester);

        eventPublisher.publishEvent(
            PersonalTrainingRegistrationNotificationEvent.create(
                requester,
                receiver,
                String.format(PT_REGISTRATION_REQUEST_MSG, requester.getName(), gym.getName()),
                NotificationType.PT_REGISTRATION_REQUEST,
                (Member) receiver,
                gymTrainer
            )
        );
        return personalTrainingService.registerPersonalTraining((Member) receiver, gymTrainer, ptRegistrationRequestDate);
    }

    @Transactional
    public void approvedPersonalTrainingRegistration(Long ptId, Long memberId, LocalDateTime registrationAllowedDate) {

        User requester = findUserById(memberId);

        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        Trainer receiver = personalTraining.getGymTrainer().getTrainer();

        eventPublisher.publishEvent(
            PersonalTrainingApproveNotificationEvent.create(
                requester,
                receiver,
                String.format(PT_REGISTRATION_ACCEPTED_MSG, requester.getName()),
                NotificationType.PT_REGISTRATION_REQUEST,
                personalTraining
            )
        );

        personalTrainingService.approvedPersonalTrainingRegistration(personalTraining, registrationAllowedDate);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
    }

    private GymTrainer getGymTrainerBy(Gym gym, Trainer trainer) {
        return gymTrainerRepository.findByTrainerAndGym(trainer, gym)
            .orElseThrow(() -> GymTrainerException.GYM_TRAINER_NOT_MAPPING);
    }

    private Gym findGymById(Long gymId) {
        return gymRepository.findById(gymId)
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);
    }
}
