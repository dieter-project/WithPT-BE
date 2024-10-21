package com.sideproject.withpt.application.gymtrainer.repository;

import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymTrainerRepository extends JpaRepository<GymTrainer, Long>, GymTrainerQueryRepository {

    Optional<GymTrainer> findByTrainerAndGym(Trainer trainer, Gym gym);

    List<GymTrainer> findAllByTrainer(Trainer trainer);
}
