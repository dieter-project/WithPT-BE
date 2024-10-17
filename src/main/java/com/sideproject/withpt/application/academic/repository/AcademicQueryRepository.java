package com.sideproject.withpt.application.academic.repository;

import com.sideproject.withpt.application.academic.service.response.AcademicResponse;
import com.sideproject.withpt.domain.trainer.Academic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface AcademicQueryRepository {

    Slice<AcademicResponse> findAllAcademicPageableByTrainerId(Long trainerId, Pageable pageable);

    boolean existAllColumns(Academic academicEntity, Long trainerId);

}
