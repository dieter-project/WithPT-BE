package com.sideproject.withpt.application.body.repository;

import static com.sideproject.withpt.domain.record.body.QBody.body;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.body.Body;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class BodyCustomRepositoryImpl implements BodyCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

//    @Override
//    public Optional<Body> findRecentBodyInfo(Long memberId, LocalDate localDate) {
//        BooleanBuilder builder = new BooleanBuilder();
//        builder.and(body.member.id.eq(memberId));
//        builder.and(body.bodyRecordDate.before(localDate).or(body.bodyRecordDate.eq(localDate)));
//
//        Body result = jpaQueryFactory.selectFrom(body)
//            .where(builder)
//            .orderBy(body.bodyRecordDate.desc())
//            .fetchFirst();
//
//        return Optional.ofNullable(result);
//    }

    @Override
    public Optional<Body> findRecentBodyInfo(Member member, LocalDate localDate) {
        Body result = jpaQueryFactory.selectFrom(body)
            .where(
                body.member.eq(member).and(
                    body.uploadDate.before(localDate).or(body.uploadDate.eq(localDate))
                )
            )
            .orderBy(body.uploadDate.desc())
            .fetchFirst();

        return Optional.ofNullable(result);
    }


    @Override
    public Optional<Body> findTodayBodyInfo(Member member, LocalDate uploadDate) {
        Body result = jpaQueryFactory
            .selectFrom(body)
            .where(
                body.member.eq(member),
                body.uploadDate.eq(uploadDate)
            )
            .fetchFirst();

        return Optional.ofNullable(result);
    }

}
