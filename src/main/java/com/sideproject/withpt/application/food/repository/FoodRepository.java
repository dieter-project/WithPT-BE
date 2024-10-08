package com.sideproject.withpt.application.food.repository;

import com.sideproject.withpt.domain.record.diet.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
