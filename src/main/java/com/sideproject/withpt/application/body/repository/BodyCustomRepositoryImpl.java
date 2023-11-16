package com.sideproject.withpt.application.body.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.record.Body;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

import static com.sideproject.withpt.domain.record.QBody.body;

@Repository
@RequiredArgsConstructor
public class BodyCustomRepositoryImpl implements BodyCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Body> findRecentBodyInfo(Long memberId, LocalDate localDate) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(body.member.id.eq(memberId));
        builder.and(body.bodyRecordDate.before(localDate).or(body.bodyRecordDate.eq(localDate)));

        Body result = jpaQueryFactory.selectFrom(body)
                .where(builder)
                .orderBy(body.bodyRecordDate.desc())
                .fetchFirst();

        return Optional.ofNullable(result);
    }


    @Override
    public Optional<Body> findTodayBodyInfo(Long memberId, LocalDate localDate) {
        Body result = jpaQueryFactory.selectFrom(body)
                .where(
                        body.member.id.eq(memberId),
                        body.bodyRecordDate.eq(localDate)
                )
                .fetchFirst();

        return Optional.ofNullable(result);
    }
}
