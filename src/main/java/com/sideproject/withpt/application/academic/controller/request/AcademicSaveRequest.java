package com.sideproject.withpt.application.academic.controller.request;

import com.sideproject.withpt.common.exception.validator.YearType;
import com.sideproject.withpt.domain.trainer.Academic;
import java.time.Year;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AcademicSaveRequest {

    @NotBlank(message = "학력명을 입력해주세요")
    private String name;

    @NotBlank(message = "전공을 입력해주세요")
    private String major;

    @YearType
    private String enrollmentYear;

    @YearType
    private String graduationYear;

    public Academic toEntity() {
        return Academic.builder()
            .name(this.name)
            .major(this.major)
            .enrollmentYear(Year.parse(this.enrollmentYear))
            .graduationYear(Year.parse(this.graduationYear))
            .build();
    }
}
