package com.sideproject.withpt.application.academic.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.common.type.AcademicInstitution;
import com.sideproject.withpt.common.type.Degree;
import com.sideproject.withpt.domain.user.trainer.Academic;
import java.time.YearMonth;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@JsonInclude(Include.NON_NULL) // null 값 제외
public class AcademicResponse {

    private Long id;
    private String name;
    private String major;
    private AcademicInstitution institution;
    private Degree degree;
    private String country;
    private YearMonth enrollmentYearMonth;
    private YearMonth graduationYearMonth;

    @QueryProjection
    public AcademicResponse(Long id, String name, String major, AcademicInstitution institution, Degree degree, String country, YearMonth enrollmentYearMonth, YearMonth graduationYearMonth) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.institution = institution;
        this.degree = degree;
        this.country = country;
        this.enrollmentYearMonth = enrollmentYearMonth;
        this.graduationYearMonth = graduationYearMonth;
    }

    public static AcademicResponse of(Academic academic) {
        return AcademicResponse.builder()
            .id(academic.getId())
            .name(academic.getName())
            .major(academic.getMajor())
            .institution(academic.getInstitution())
            .degree(academic.getDegree())
            .country(academic.getCountry())
            .enrollmentYearMonth(academic.getEnrollmentYearMonth())
            .graduationYearMonth(academic.getGraduationYearMonth())
            .build();
    }
}
