package com.sideproject.withpt.application.academic.repository;

import static com.sideproject.withpt.domain.trainer.QAcademic.academic;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.academic.service.response.AcademicResponse;
import com.sideproject.withpt.application.academic.service.response.QAcademicResponse;
import com.sideproject.withpt.common.type.AcademicInstitution;
import com.sideproject.withpt.common.type.Degree;
import com.sideproject.withpt.domain.user.trainer.Academic;
import java.time.YearMonth;
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
public class AcademicQueryRepositoryImpl implements AcademicQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<AcademicResponse> findAllAcademicPageableByTrainerId(Long trainerId, Pageable pageable) {

        List<AcademicResponse> contents = jpaQueryFactory
            .select(
                new QAcademicResponse(
                    academic.id,
                    academic.name,
                    academic.major,
                    academic.institution,
                    academic.degree,
                    academic.country,
                    academic.enrollmentYearMonth,
                    academic.graduationYearMonth
                )
            )
            .from(academic)
            .where(academic.trainer.id.eq(trainerId))
            .orderBy(academic.enrollmentYearMonth.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        boolean hasNext = false;

        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    @Override
    public boolean existAllColumns(Academic academicEntity, Long trainerId) {

        Integer fetchOne = jpaQueryFactory
            .selectOne()
            .from(academic)
            .where(
                academic.trainer.id.eq(trainerId),
                nameEq(academicEntity.getName()),
                majorEq(academicEntity.getMajor()),
                institutionEq(academicEntity.getInstitution()),
                degreeEq(academicEntity.getDegree()),
                countryEq(academicEntity.getCountry()),
                enrollmentYearMonthEq(academicEntity.getEnrollmentYearMonth()),
                graduationYearMonthEq(academicEntity.getGraduationYearMonth())
            ).fetchFirst();

        return fetchOne != null;
    }

    private BooleanExpression nameEq(String name) {
        return StringUtils.hasText(name) ? academic.name.eq(name) : null;
    }

    private BooleanExpression majorEq(String major) {
        return StringUtils.hasText(major) ? academic.major.eq(major) : null;
    }

    private BooleanExpression institutionEq(AcademicInstitution institution) {
        return ObjectUtils.isEmpty(institution) ? null : academic.institution.eq(institution);
    }

    private BooleanExpression degreeEq(Degree degree) {
        return ObjectUtils.isEmpty(degree) ? null : academic.degree.eq(degree);
    }

    private BooleanExpression countryEq(String country) {
        return StringUtils.hasText(country) ? academic.country.eq(country) : null;
    }

    private BooleanExpression enrollmentYearMonthEq(YearMonth enrollmentYear) {
        return ObjectUtils.isEmpty(enrollmentYear) ? null : academic.enrollmentYearMonth.eq(enrollmentYear);
    }

    private BooleanExpression graduationYearMonthEq(YearMonth graduationYear) {
        return ObjectUtils.isEmpty(graduationYear) ? null : academic.graduationYearMonth.eq(graduationYear);
    }
}
