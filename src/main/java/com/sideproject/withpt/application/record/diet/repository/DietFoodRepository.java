package com.sideproject.withpt.application.record.diet.repository;

import com.sideproject.withpt.domain.record.diet.DietFood;
import com.sideproject.withpt.domain.record.diet.DietInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DietFoodRepository extends JpaRepository<DietFood, Long> {
    List<DietFood> findAllByIdInAndDietInfo(List<Long> ids, DietInfo dietInfo);
    List<DietFood> findAllByDietInfoOrderById(DietInfo dietInfo);

    @Modifying
    @Query("delete from DietFood df where df.id in :ids and df.dietInfo = :dietInfo")
    void deleteAllByIdInAndDietInfo(@Param("ids") List<Long> ids, @Param("dietInfo") DietInfo dietInfo);
}
