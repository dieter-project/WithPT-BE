package com.sideproject.withpt.application.body.repository;

import com.sideproject.withpt.domain.record.Body;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BodyCustomRepository {
    Optional<Body> findRecentBodyInfo(Long memberId, LocalDateTime dateTime);
    Optional<Body> findTodayBodyInfo(Long memberId, LocalDateTime weightRecordDate);
}
