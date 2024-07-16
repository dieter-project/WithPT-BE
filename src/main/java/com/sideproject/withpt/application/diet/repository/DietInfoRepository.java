package com.sideproject.withpt.application.diet.repository;

import com.sideproject.withpt.domain.record.diet.DietInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietInfoRepository extends JpaRepository<DietInfo, Long> {

}