package com.sideproject.withpt.application.record.body.repository;

import static com.sideproject.withpt.domain.record.body.QBody.body;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.record.body.Body;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class BodyCustomRepositoryImpl implements BodyCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Body> findLatestBodyInfoBy(Member member, LocalDate uploadDate) {
        return Optional.ofNullable(
            jpaQueryFactory.selectFrom(body)
                .where(body.member.eq(member)
                    .and(body.skeletalMuscle.gt(0.0))
                    .or(body.bodyFatPercentage.gt(0.0))
                    .or(body.bmi.gt(0.0))
                    .and(body.uploadDate.loe(uploadDate))
                )
                .orderBy(body.uploadDate.desc())
                .fetchFirst()
        );
    }

    @Override
    public List<Body> findLatestWeightsBy(Member member, LocalDate uploadDate) {
        return jpaQueryFactory
            .selectFrom(body)
            .where(body.member.eq(member)
                .and(body.weight.gt(0.0)) // gt >
                .and(body.uploadDate.loe(uploadDate)) // loe <=
            )
            .orderBy(body.uploadDate.desc())
            .limit(2)
            .fetch();
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

    @Override
    public Map<LocalDate, Body> findBodyByYearMonth(Member member, int year, int month) {
        String searchYearMonth = YearMonth.of(year, month).toString();
        StringTemplate bodyUploadDate = Expressions.stringTemplate(
            "DATE_FORMAT({0}, '%Y-%m')",
            body.uploadDate
        );

        return jpaQueryFactory.selectFrom(body)
            .where(body.member.eq(member).and(bodyUploadDate.eq(searchYearMonth)))
            .orderBy(body.uploadDate.asc())
            .fetch()
            .stream()
            .collect(
                Collectors.toMap(Body::getUploadDate,
                    body -> body)
            );
    }
}
