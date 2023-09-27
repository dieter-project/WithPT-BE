package com.sideproject.withpt.application.body.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.record.Body;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
        Body result = jpaQueryFactory.selectFrom(body)
                .where(
                        body.member.id.eq(memberId),
                        body.bodyRecordDate.year().eq(weightRecordDate.getYear()),
                        body.bodyRecordDate.month().eq(weightRecordDate.getMonthValue()),
                        body.bodyRecordDate.dayOfMonth().eq(weightRecordDate.getDayOfMonth())
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
