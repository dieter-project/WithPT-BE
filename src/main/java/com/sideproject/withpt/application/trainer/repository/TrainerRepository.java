package com.sideproject.withpt.application.trainer.repository;

import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByEmailAndAuthProvider(String email, AuthProvider authProvider);
    boolean existsByEmailAndAuthProvider(String email, AuthProvider authProvider);

    boolean existsByEmail(String email);

    Optional<Trainer> findById(Long id);
}
