package com.sideproject.withpt.application.education.repository;

import com.sideproject.withpt.application.education.controller.reponse.EducationResponse;
import com.sideproject.withpt.domain.user.trainer.Education;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface EducationQueryRepository {

    Slice<EducationResponse> findAllEducationPageableByTrainerId(Long trainerId, Pageable pageable);

    boolean existAllColumns(Education educationEntity, Long trainerId);
}
