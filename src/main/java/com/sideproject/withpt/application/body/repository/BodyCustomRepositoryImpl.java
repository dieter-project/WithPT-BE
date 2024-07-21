package com.sideproject.withpt.application.body.repository;

import static com.sideproject.withpt.domain.record.body.QBody.body;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.body.controller.response.WeightInfoResponse;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.body.Body;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class BodyCustomRepositoryImpl implements BodyCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public WeightInfoResponse findRecentBodyInfo(Member member, LocalDate uploadDate) {

        List<Body> weightResult = jpaQueryFactory.selectFrom(body)
            .where(body.member.eq(member)
                .and(body.weight.ne(0.0))
                .and(body.uploadDate.loe(uploadDate))
            )
            .orderBy(body.uploadDate.desc())
            .limit(2)
            .fetch();

        Optional<Body> bodyInfoResult = Optional.ofNullable(
            jpaQueryFactory.selectFrom(body)
                .where(body.member.eq(member)
                    .and(body.weight.eq(0.0))
                    .and(body.uploadDate.loe(uploadDate))
                )
                .orderBy(body.uploadDate.desc())
                .fetchFirst()
        );

        return WeightInfoResponse.from(weightResult, bodyInfoResult);
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
