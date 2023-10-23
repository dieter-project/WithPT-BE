package com.sideproject.withpt.application.academic.controller.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.domain.trainer.Academic;
import java.time.Year;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
public class AcademicResponse {

    private Long id;
    private String name;
    private String major;
    private Year enrollmentYear;
    private Year graduationYear;

    @QueryProjection
    public AcademicResponse(Long id, String name, String major, Year enrollmentYear, Year graduationYear) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.enrollmentYear = enrollmentYear;
        this.graduationYear = graduationYear;
    }

    public static AcademicResponse of(Academic academic) {
        return AcademicResponse.builder()
            .id(academic.getId())
            .name(academic.getName())
            .major(academic.getMajor())
            .enrollmentYear(academic.getEnrollmentYear())
            .graduationYear(academic.getGraduationYear())
            .build();
    }
}
