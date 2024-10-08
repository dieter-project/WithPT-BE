package com.sideproject.withpt.application.pt.repository;

import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalTrainingRepository extends JpaRepository<PersonalTraining, Long>, PersonalTrainingQueryRepository {

    Optional<PersonalTraining> findByMemberAndGymTrainer(Member member, GymTrainer gymTrainer);

    boolean existsByMemberAndGymTrainer(Member member, GymTrainer gymTrainer);
}
