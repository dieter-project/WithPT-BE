package com.sideproject.withpt.application.academic.controller.request;

import com.sideproject.withpt.common.exception.validator.YearType;
import java.time.Year;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AcademicEditRequest {

    @NotNull(message = "학력 ID는 필수입니다.")
    private Long id;

    @NotBlank(message = "학력명을 입력해주세요")
    private String name;

    @NotBlank(message = "전공을 입력해주세요")
    private String major;

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
