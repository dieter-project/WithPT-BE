package com.sideproject.withpt.application.record.exercise.repository;

import com.sideproject.withpt.domain.record.exercise.BodyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BodyCategoryRepository extends JpaRepository<BodyCategory, Long> {

}
