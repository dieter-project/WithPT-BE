package com.sideproject.withpt.application.career.repository;

import static com.sideproject.withpt.domain.trainer.QCareer.career;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.career.controller.response.CareerResponse;
import com.sideproject.withpt.application.career.controller.response.QCareerResponse;
import com.sideproject.withpt.common.type.EmploymentStatus;
import com.sideproject.withpt.domain.trainer.Career;
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
public class CareerQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Slice<CareerResponse> findAllCareerPageableByTrainerId(Long trainerId, Pageable pageable) {
        List<CareerResponse> careerResponseList = jpaQueryFactory
            .select(
                new QCareerResponse(
                    career.id,
                    career.centerName,
                    career.jobPosition,
                    career.status,
                    career.startOfWorkYearMonth,
                    career.endOfWorkYearMonth
                )
            )
            .from(career)
            .where(career.trainer.id.eq(trainerId))
            .orderBy(career.startOfWorkYearMonth.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<CareerResponse> content = new ArrayList<>(careerResponseList);

        boolean hasText = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasText = true;
        }

        return new SliceImpl<>(content, pageable, hasText);
    }

    public boolean existAllColumns(Career careerEntity, Long trainerId) {
        Integer fetchOne = jpaQueryFactory
            .selectOne()
            .from(career)
            .where(
                career.trainer.id.eq(trainerId),
                centerNameEq(careerEntity.getCenterName()),
                jobPositionEq(careerEntity.getJobPosition()),
                statusEq(careerEntity.getStatus()),
                startOfWorkYearMonthEq(careerEntity.getStartOfWorkYearMonth()),
                endOfWorkYearMonthEq(careerEntity.getEndOfWorkYearMonth())
            ).fetchFirst();

        return fetchOne != null;
    }


    private BooleanExpression centerNameEq(String centerName) {
        return StringUtils.hasText(centerName) ? career.centerName.eq(centerName) : null;
    }

    private BooleanExpression jobPositionEq(String jobPosition) {
        return StringUtils.hasText(jobPosition) ? career.jobPosition.eq(jobPosition) : null;
    }

    private BooleanExpression statusEq(EmploymentStatus status) {
        return ObjectUtils.isEmpty(status) ? null : career.status.eq(status);
    }

    private BooleanExpression startOfWorkYearMonthEq(YearMonth startOfWorkYearMonth) {
        return ObjectUtils.isEmpty(startOfWorkYearMonth) ? null : career.startOfWorkYearMonth.eq(startOfWorkYearMonth);
    }

    private BooleanExpression endOfWorkYearMonthEq(YearMonth endOfWorkYearMonth) {
        return ObjectUtils.isEmpty(endOfWorkYearMonth) ? null : career.endOfWorkYearMonth.eq(endOfWorkYearMonth);
    }
}
