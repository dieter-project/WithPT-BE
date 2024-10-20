package com.sideproject.withpt.application.certificate.repository;

import com.sideproject.withpt.domain.trainer.Certificate;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long>, CertificateQueryRepository {

    Optional<Certificate> findByIdAndTrainer(Long certificateId, Trainer trainer);
}
