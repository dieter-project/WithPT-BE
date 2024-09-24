package com.sideproject.withpt.application.gymtrainer.repository;

import com.sideproject.withpt.domain.gym.GymTrainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymTrainerRepository extends JpaRepository<GymTrainer, Long> {

}
