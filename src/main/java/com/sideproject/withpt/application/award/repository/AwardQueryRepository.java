package com.sideproject.withpt.application.award.repository;

import com.sideproject.withpt.application.award.service.reponse.AwardResponse;
import com.sideproject.withpt.domain.user.trainer.Award;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface AwardQueryRepository {

    Slice<AwardResponse> findAllAwardPageableByTrainerId(Long trainerId, Pageable pageable);

    boolean existAllColumns(Award awardEntity, Long trainerId);

}
