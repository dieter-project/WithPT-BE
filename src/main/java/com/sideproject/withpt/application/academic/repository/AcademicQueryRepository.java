package com.sideproject.withpt.application.academic.repository;

import static com.sideproject.withpt.domain.trainer.QAcademic.academic;
import static com.sideproject.withpt.domain.trainer.QCareer.career;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.academic.controller.response.AcademicResponse;
import com.sideproject.withpt.application.academic.controller.response.QAcademicResponse;
import com.sideproject.withpt.domain.trainer.Academic;
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
public class AcademicQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;


    public Slice<AcademicResponse> findAllAcademicPageableByTrainerId(Long trainerId, Pageable pageable) {

        List<AcademicResponse> academicResponseList = jpaQueryFactory
            .select(
                new QAcademicResponse(
                    academic.id,
                    academic.name,
                    academic.major,
                    academic.enrollmentYear,
                    academic.graduationYear
                )
            )
            .from(academic)
            .where(academic.trainer.id.eq(trainerId))
            .orderBy(academic.enrollmentYear.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<AcademicResponse> content = new ArrayList<>(academicResponseList);

        boolean hasNext = false;

        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    public boolean existAllColumns(Academic academicEntity, Long trainerId) {

        Integer fetchOne = jpaQueryFactory
            .selectOne()
            .from(academic)
            .where(
                academic.trainer.id.eq(trainerId),
                nameEq(academicEntity.getName()),
                majorEq(academicEntity.getMajor()),
                enrollmentYearEq(academicEntity.getEnrollmentYear()),
                graduationYearEq(academicEntity.getGraduationYear())
            ).fetchFirst();

        return fetchOne != null;
    }

    private BooleanExpression nameEq(String name) {
        return StringUtils.hasText(name) ? academic.name.eq(name) : null;
    }

    private BooleanExpression majorEq(String major) {
        return StringUtils.hasText(major) ? academic.major.eq(major) : null;
    }

    private BooleanExpression enrollmentYearEq(Year enrollmentYear) {
        return ObjectUtils.isEmpty(enrollmentYear) ? null : academic.enrollmentYear.eq(enrollmentYear);
    }

    private BooleanExpression graduationYearEq(Year graduationYear) {
        return ObjectUtils.isEmpty(graduationYear) ? null : academic.graduationYear.eq(graduationYear);
    }
}
