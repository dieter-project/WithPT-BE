package com.sideproject.withpt.application.body.repository;

import com.sideproject.withpt.domain.record.Body;

import java.time.LocalDate;
import java.util.Optional;

public interface BodyCustomRepository {
    Optional<Body> findRecentBodyInfo(Long memberId, LocalDate dateTime);
    Optional<Body> findTodayBodyInfo(Long memberId, LocalDate weightRecordDate);
}
