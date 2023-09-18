package com.sideproject.withpt.application.gym.repositoy;

import com.sideproject.withpt.domain.gym.Gym;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {
    Optional<Gym> findByName(String name);
}
