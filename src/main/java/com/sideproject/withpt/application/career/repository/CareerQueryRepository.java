package com.sideproject.withpt.application.career.repository;

import com.sideproject.withpt.application.career.service.response.CareerResponse;
import com.sideproject.withpt.domain.user.trainer.Career;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CareerQueryRepository {

    Slice<CareerResponse> findAllCareerPageableByTrainerId(Long trainerId, Pageable pageable);

    boolean existAllColumns(Career careerEntity, Long trainerId);
}
