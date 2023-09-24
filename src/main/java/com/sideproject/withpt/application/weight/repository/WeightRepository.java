package com.sideproject.withpt.application.weight.repository;

import com.sideproject.withpt.domain.record.Weight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Long> {
    Optional<Weight> findTop1ByMemberIdOrderByWeightRecordDate(Long memberId);
}
