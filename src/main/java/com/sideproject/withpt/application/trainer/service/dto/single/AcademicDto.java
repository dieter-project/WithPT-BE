package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.common.type.AcademicInstitution;
import com.sideproject.withpt.common.type.Degree;
import com.sideproject.withpt.domain.user.trainer.Academic;
import java.time.YearMonth;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AcademicDto {

    private static final String NO_MAJOR = "전공 없음";

    private final String name;
    private String major;
    private final AcademicInstitution institution;
    private Degree degree;
    private final String country;
    private final YearMonth enrollmentYearMonth;
    private final YearMonth graduationYearMonth;

    @Builder
    private AcademicDto(String name, String major, AcademicInstitution institution, Degree degree, String country, YearMonth enrollmentYearMonth, YearMonth graduationYearMonth) {
        this.name = name;
        this.major = major;
        this.institution = institution;
        this.degree = degree;
        this.country = country;
        this.enrollmentYearMonth = enrollmentYearMonth;
        this.graduationYearMonth = graduationYearMonth;
    }

    public Academic toEntity() {
        // 고등학교 -> 전공 없음
        // 학위 -> 고등학교 졸업장
        if (institution == AcademicInstitution.HIGH_SCHOOL) {
            this.major = NO_MAJOR;
            this.degree = Degree.HIGH_SCHOOL_DIPLOMA;
        }

        // 기관이 국내 대학이면 degree는 학사
        if (institution != AcademicInstitution.HIGH_SCHOOL && institution != AcademicInstitution.OVERSEAS_UNIVERSITY) {
            this.degree = Degree.BACHELOR;
        }

        return Academic.builder()
            .name(this.name)
            .major(this.major)
            .institution(this.institution)
            .degree(this.degree)
            .country(this.country)
            .enrollmentYearMonth(this.enrollmentYearMonth)
            .graduationYearMonth(this.graduationYearMonth)
            .build();
    }
}
