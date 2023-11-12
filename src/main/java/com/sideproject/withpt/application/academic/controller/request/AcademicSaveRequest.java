package com.sideproject.withpt.application.academic.controller.request;

import com.sideproject.withpt.application.type.AcademicInstitution;
import com.sideproject.withpt.application.type.Degree;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.exception.validator.YearType;
import com.sideproject.withpt.domain.trainer.Academic;
import java.time.Year;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AcademicSaveRequest {

    private static final String NO_MAJOR = "전공 없음";

    @NotBlank(message = "학력명을 입력해주세요")
    private String name;

    private String major;

    @ValidEnum(regexp = "FOUR_YEAR_UNIVERSITY|"
        + "THREE_YEAR_COLLEGE|"
        + "TWO_YEAR_COLLEGE|"
        + "GRADUATE_SCHOOL|"
        + "OVERSEAS_UNIVERSITY|"
        + "HIGH_SCHOOL", enumClass = AcademicInstitution.class)
    private AcademicInstitution institution;

    @Default
    @ValidEnum(regexp = "HIGH_SCHOOL_DIPLOMA|ASSOCIATE|BACHELOR|MASTER|DOCTORATE", enumClass = Degree.class)
    private Degree degree = Degree.HIGH_SCHOOL_DIPLOMA;

    @Default
    private String country = "Korea";

    @YearType
    private String enrollmentYear;

    @YearType
    private String graduationYear;

    public Year getEnrollmentYear() {
        return Year.parse(this.enrollmentYear);
    }

    public Year getGraduationYear() {
        return Year.parse(this.graduationYear);
    }

    public Academic toEntity() {
        if(!StringUtils.hasText(country)) {
            country = "Korea";
        }

        if(institution == AcademicInstitution.HIGH_SCHOOL) {
            this.major = NO_MAJOR;
            this.degree = Degree.HIGH_SCHOOL_DIPLOMA;
        }

        // 기관이 국내 대학이면 degree는 학사
        if(institution != AcademicInstitution.HIGH_SCHOOL && institution != AcademicInstitution.OVERSEAS_UNIVERSITY) {
            this.degree = Degree.BACHELOR;
        }

        return Academic.builder()
            .name(this.name)
            .major(this.major)
            .institution(this.institution)
            .degree(this.degree)
            .country(this.country)
            .enrollmentYear(this.getEnrollmentYear())
            .graduationYear(this.getGraduationYear())
            .build();
    }
}
