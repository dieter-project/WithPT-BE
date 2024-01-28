package com.sideproject.withpt.application.gym.service;

import com.sideproject.withpt.application.gym.controller.response.TrainerAllGymsResponse;
import com.sideproject.withpt.application.gym.controller.response.TrainerAllGymsResponse.GymResponse;
import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymQueryRepository;
import com.sideproject.withpt.application.lesson.controller.response.SearchMemberResponse;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GymQueryService {

    private final TrainerService trainerService;
    private final GymService gymService;
    private final GymQueryRepository gymQueryRepository;

    public TrainerAllGymsResponse listOfTrainerAllGyms(Long trainerId) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        List<GymTrainer> allGymsByTrainer = gymQueryRepository.findAllGymsByTrainer(trainer);
        if (allGymsByTrainer == null || allGymsByTrainer.isEmpty()) {
            throw GymException.GYM_TRAINER_NOT_MAPPING;
        }

        return TrainerAllGymsResponse.from(
            allGymsByTrainer.stream()
                .map(gymTrainer -> GymResponse.from(gymTrainer.getGym()))
                .collect(Collectors.toList())
        );
    }

    public Slice<Gym> listOfAllGymsByPageable(Long trainerId, Pageable pageable) {
        Trainer trainer = trainerService.getTrainerById(trainerId);
        return gymQueryRepository.findAllTrainerGymsByPageable(trainer, pageable);
    }

    public Slice<SearchMemberResponse> searchMembersByGymIdAndName(Long gymId, Long trainerId, String name, Pageable pageable) {
        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

        // TODO : 예외 처리 추가
        return gymQueryRepository.findAllMembersByGymIdAndName(trainer, gym, name, pageable);
    }
}
