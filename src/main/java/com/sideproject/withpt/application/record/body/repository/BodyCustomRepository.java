package com.sideproject.withpt.application.record.body.repository;

import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.body.Body;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BodyCustomRepository {

    Optional<Body> findLatestBodyInfoBy(Member member, LocalDate uploadDate);

    List<Body> findLatestWeightsBy(Member member, LocalDate uploadDate);

    Optional<Body> findTodayBodyInfo(Member member, LocalDate weightRecordDate);

    Map<LocalDate, Body> findBodyByYearMonth(Member member, int year, int month);
}
