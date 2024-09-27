package com.sideproject.withpt.application.gymtrainer.repository;

import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymTrainerRepository extends JpaRepository<GymTrainer, Long> {

    Optional<GymTrainer> findByTrainerAndGym(Trainer trainer, Gym gym);
}
