package com.sideproject.withpt.application.diet.repository;

import com.sideproject.withpt.domain.record.diet.DietFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietFoodRepository extends JpaRepository<DietFood, Long> {

}
