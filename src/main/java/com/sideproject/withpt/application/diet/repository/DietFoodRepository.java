package com.sideproject.withpt.application.diet.repository;

import com.sideproject.withpt.domain.record.diet.DietFood;
import com.sideproject.withpt.domain.record.diet.DietInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietFoodRepository extends JpaRepository<DietFood, Long> {
    List<DietFood> findAllByIdInAndDietInfo(List<Long> ids, DietInfo dietInfo);
    void deleteAllByIdInAndDietInfo(List<Long> ids, DietInfo dietInfo);
}
