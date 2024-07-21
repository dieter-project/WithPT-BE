package com.sideproject.withpt.application.body.repository;

import com.sideproject.withpt.application.body.controller.response.WeightInfoResponse;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.body.Body;
import java.time.LocalDate;
import java.util.Optional;

public interface BodyCustomRepository {

    //    Optional<Body> findRecentBodyInfo(Long memberId, LocalDate dateTime);
    WeightInfoResponse findRecentBodyInfo(Member member, LocalDate dateTime);

    //    Optional<Body> findTodayBodyInfo(Long memberId, LocalDate weightRecordDate);
    Optional<Body> findTodayBodyInfo(Member member, LocalDate weightRecordDate);
}
