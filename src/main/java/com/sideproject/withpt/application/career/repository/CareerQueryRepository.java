package com.sideproject.withpt.application.career.repository;

import com.sideproject.withpt.application.career.controller.response.CareerResponse;
import com.sideproject.withpt.domain.trainer.Career;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CareerQueryRepository {

    Slice<CareerResponse> findAllCareerPageableByTrainerId(Long trainerId, Pageable pageable);

    boolean existAllColumns(Career careerEntity, Long trainerId);
}
