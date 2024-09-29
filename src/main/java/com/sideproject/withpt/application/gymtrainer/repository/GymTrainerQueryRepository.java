package com.sideproject.withpt.application.gymtrainer.repository;

import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GymTrainerQueryRepository {

    Slice<GymTrainer> findAllPageableByTrainer(Trainer trainer, Pageable pageable);
}
