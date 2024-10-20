package com.sideproject.withpt.application.career.repository;

import com.sideproject.withpt.domain.trainer.Career;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long>, CareerQueryRepository {

    Optional<Career> findByIdAndTrainer(Long careerId, Trainer trainer);
}
