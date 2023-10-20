package com.sideproject.withpt.application.diet.repository;

import com.sideproject.withpt.domain.record.Diets;
import com.sideproject.withpt.domain.record.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByDietsId(Long dietsId);
}
