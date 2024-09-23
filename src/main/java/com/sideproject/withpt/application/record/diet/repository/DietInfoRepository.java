package com.sideproject.withpt.application.record.diet.repository;

import com.sideproject.withpt.domain.record.diet.DietInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietInfoRepository extends JpaRepository<DietInfo, Long> {

}
