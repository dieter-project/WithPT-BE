package com.sideproject.withpt.application.body.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.record.Body;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.sideproject.withpt.domain.record.QBody.body;

@Repository
@RequiredArgsConstructor
public class BodyCustomRepositoryImpl implements BodyCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Body> findRecentBodyInfo(Long memberId, LocalDateTime dateTime) {
        Body result = jpaQueryFactory.selectFrom(body)
                .where(body.member.id.eq(memberId),
                        body.bodyRecordDate.before(dateTime))
                .orderBy(body.bodyRecordDate.desc())
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Body> findTodayBodyInfo(Long memberId, LocalDateTime weightRecordDate) {
        LocalDate localDate = weightRecordDate.toLocalDate();

        Body result = jpaQueryFactory.selectFrom(body)
                .where(
                        body.member.id.eq(memberId),
                        body.bodyRecordDate.between(localDate.atStartOfDay(), localDate.atTime(23, 59, 59))
                )
                .fetchFirst();

        return Optional.ofNullable(result);
    }
}
