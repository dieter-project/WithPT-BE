package com.sideproject.withpt.application.academic.controller.request;

import com.sideproject.withpt.application.type.AcademicInstitution;
import com.sideproject.withpt.application.type.Degree;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.exception.validator.YearType;
import java.time.Year;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AcademicEditRequest {

    private static final String NO_MAJOR = "전공 없음";

    @NotNull(message = "학력 ID는 필수입니다.")
    private Long id;

    @NotBlank(message = "학력명을 입력해주세요")
    private String name;

    @Default
    private String major = NO_MAJOR;

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
}
