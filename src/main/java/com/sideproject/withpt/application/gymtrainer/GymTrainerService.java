package com.sideproject.withpt.application.gymtrainer;

import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GymTrainerService {

    private final GymTrainerRepository gymTrainerRepository;

    @Transactional
    public List<GymTrainer> registerGymTrainers(List<Gym> gyms, Trainer trainer) {
        return gymTrainerRepository.saveAll(
            gyms.stream()
                .map(gym -> GymTrainer.createGymTrainer(gym, trainer))
                .collect(Collectors.toList())
        );
    }
}
