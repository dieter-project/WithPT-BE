package com.sideproject.withpt.application.Food.repository;

import com.sideproject.withpt.domain.record.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
