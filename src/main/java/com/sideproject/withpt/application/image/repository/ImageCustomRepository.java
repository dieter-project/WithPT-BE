package com.sideproject.withpt.application.image.repository;

import com.sideproject.withpt.application.body.dto.response.BodyImageResponse;
import com.sideproject.withpt.application.type.Usages;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ImageCustomRepository {
    Slice<BodyImageResponse> findAllBodyImage(Pageable pageable, Long memberId, Usages usages);
}
