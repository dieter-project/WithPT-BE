package com.sideproject.withpt.application.award.repository;


import static com.sideproject.withpt.domain.user.trainer.QAward.award;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.award.service.reponse.AwardResponse;
import com.sideproject.withpt.application.award.service.reponse.QAwardResponse;
import com.sideproject.withpt.domain.user.trainer.Award;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AwardQueryRepositoryImpl implements AwardQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<AwardResponse> findAllAwardPageableByTrainerId(Long trainerId, Pageable pageable) {

        List<AwardResponse> awardResponseList = jpaQueryFactory
            .select(
                new QAwardResponse(
                    award.id,
                    award.name,
                    award.institution,
                    award.acquisitionYearMonth
                )
            )
            .from(award)
            .where(award.trainer.id.eq(trainerId))
            .orderBy(award.acquisitionYearMonth.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<AwardResponse> content = new ArrayList<>(awardResponseList);

        boolean hasNext = false;

        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public boolean existAllColumns(Award awardEntity, Long trainerId) {

        Integer fetchOne = jpaQueryFactory
            .selectOne()
            .from(award)
            .where(
                award.trainer.id.eq(trainerId),
                nameEq(awardEntity.getName()),
                institutionEq(awardEntity.getInstitution()),
                acquisitionYearMonthEq(awardEntity.getAcquisitionYearMonth())
            ).fetchFirst();

        return fetchOne != null;
    }

    private BooleanExpression nameEq(String name) {
        return StringUtils.hasText(name) ? award.name.eq(name) : null;
    }

    private BooleanExpression institutionEq(String institution) {
        return StringUtils.hasText(institution) ? award.institution.eq(institution) : null;
    }

    private BooleanExpression acquisitionYearMonthEq(YearMonth acquisitionYearMonth) {
        return ObjectUtils.isEmpty(acquisitionYearMonth) ? null : award.acquisitionYearMonth.eq(acquisitionYearMonth);
    }
}
