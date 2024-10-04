package com.sideproject.withpt.application.gymtrainer.repository;

import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GymTrainerQueryRepository {

    Slice<GymTrainer> findAllPageableByTrainer(Trainer trainer, Pageable pageable);
    List<GymTrainer> findAllTrainerAndGym(Trainer trainer, Gym gym);
}
