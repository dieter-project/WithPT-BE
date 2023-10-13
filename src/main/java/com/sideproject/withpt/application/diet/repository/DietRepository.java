package com.sideproject.withpt.application.diet.repository;

import com.sideproject.withpt.domain.record.Diet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietRepository extends JpaRepository<Diet, Long> {
}
