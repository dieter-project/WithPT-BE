package com.sideproject.withpt.application.award.repository;

import static com.sideproject.withpt.domain.trainer.QAward.*;
import static com.sideproject.withpt.domain.trainer.QCertificate.certificate;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.award.controller.reponse.AwardResponse;
import com.sideproject.withpt.application.award.controller.reponse.QAwardResponse;
import com.sideproject.withpt.application.certificate.controller.reponse.QCertificateResponse;
import com.sideproject.withpt.domain.trainer.Award;
import com.sideproject.withpt.domain.trainer.Certificate;
import com.sideproject.withpt.domain.trainer.QAward;
import java.time.Year;
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
public class AwardQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Slice<AwardResponse> findAllAwardPageableByTrainerId(Long trainerId, Pageable pageable) {

        List<AwardResponse> awardResponseList = jpaQueryFactory
            .select(
                new QAwardResponse(
                    award.id,
                    award.name,
                    award.institution,
                    award.acquisitionYear
                )
            )
            .from(award)
            .where(award.trainer.id.eq(trainerId))
            .orderBy(award.acquisitionYear.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<AwardResponse> content = new ArrayList<>(awardResponseList);

        boolean hasNext = false;

        if(content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    public boolean existAllColumns(Award awardEntity, Long trainerId) {

        Integer fetchOne = jpaQueryFactory
            .selectOne()
            .from(award)
            .where(
                award.trainer.id.eq(trainerId),
                nameEq(awardEntity.getName()),
                institutionEq(awardEntity.getInstitution()),
                acquisitionYearEq(awardEntity.getAcquisitionYear())
            ).fetchFirst();

        return fetchOne != null;
    }

    private BooleanExpression nameEq(String name) {
        return StringUtils.hasText(name) ? award.name.eq(name) : null;
    }

    private BooleanExpression institutionEq(String institution) {
        return StringUtils.hasText(institution) ? award.institution.eq(institution) : null;
    }

    private BooleanExpression acquisitionYearEq(Year acquisitionYear) {
        return ObjectUtils.isEmpty(acquisitionYear) ? null : award.acquisitionYear.eq(acquisitionYear);
    }
}
