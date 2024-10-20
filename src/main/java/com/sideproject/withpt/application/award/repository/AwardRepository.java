package com.sideproject.withpt.application.award.repository;

import com.sideproject.withpt.domain.trainer.Award;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AwardRepository extends JpaRepository<Award, Long>, AwardQueryRepository {

    Optional<Award> findByIdAndTrainer(Long awardId, Trainer trainer);
}
