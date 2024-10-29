package com.sideproject.withpt.application.education.repository;

import static com.sideproject.withpt.domain.user.trainer.QEducation.education;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.education.service.reponse.EducationResponse;
import com.sideproject.withpt.application.education.service.reponse.QEducationResponse;
import com.sideproject.withpt.domain.user.trainer.Education;
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
public class EducationQueryRepositoryImpl implements EducationQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<EducationResponse> findAllEducationPageableByTrainerId(Long trainerId, Pageable pageable) {

        List<EducationResponse> educationResponseList = jpaQueryFactory
            .select(
                new QEducationResponse(
                    education.id,
                    education.name,
                    education.institution,
                    education.acquisitionYearMonth
                )
            )
            .from(education)
            .where(education.trainer.id.eq(trainerId))
            .orderBy(education.acquisitionYearMonth.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();
        List<EducationResponse> content = new ArrayList<>(educationResponseList);

        boolean hasNext = false;

        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public boolean existAllColumns(Education educationEntity, Long trainerId) {

        Integer fetchOne = jpaQueryFactory
            .selectOne()
            .from(education)
            .where(
                education.trainer.id.eq(trainerId),
                nameEq(educationEntity.getName()),
                institutionEq(educationEntity.getInstitution()),
                acquisitionYearMonthEq(educationEntity.getAcquisitionYearMonth())
            ).fetchFirst();

        return fetchOne != null;
    }

    private BooleanExpression nameEq(String name) {
        return StringUtils.hasText(name) ? education.name.eq(name) : null;
    }

    private BooleanExpression institutionEq(String institution) {
        return StringUtils.hasText(institution) ? education.institution.eq(institution) : null;
    }

    private BooleanExpression acquisitionYearMonthEq(YearMonth acquisitionYearMonth) {
        return ObjectUtils.isEmpty(acquisitionYearMonth) ? null : education.acquisitionYearMonth.eq(acquisitionYearMonth);
    }
}
