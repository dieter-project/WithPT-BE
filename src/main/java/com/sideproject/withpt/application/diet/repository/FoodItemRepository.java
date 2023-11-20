package com.sideproject.withpt.application.diet.repository;

import com.sideproject.withpt.domain.record.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    void deleteByDietId(Long DietId);
}
