package com.sideproject.withpt.application.education.repository;

import com.sideproject.withpt.domain.trainer.Award;
import com.sideproject.withpt.domain.trainer.Education;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {

    Optional<Education> findByIdAndTrainer(Long educationId, Trainer trainer);
}
