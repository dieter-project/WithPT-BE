package com.sideproject.withpt.application.academic.repository;

import com.sideproject.withpt.domain.trainer.Academic;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicRepository extends JpaRepository<Academic, Long>, AcademicQueryRepository {

    Optional<Academic> findByIdAndTrainer(Long academicId, Trainer trainer);
}
