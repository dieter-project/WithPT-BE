package com.sideproject.withpt.application.education.controller.request;

import com.sideproject.withpt.common.exception.validator.YearMonthType;
import com.sideproject.withpt.common.exception.validator.YearType;
import java.time.Year;
import java.time.YearMonth;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EducationEditRequest {

    @NotNull(message = "교육 Id는 필수입니다.")
    private Long id;

    @NotBlank(message = "교육명을 입력해주세요")
    private String name;

    @NotBlank(message = "기관명을 입력해주세요")
    private String institution;

    @YearMonthType
    private String acquisitionYearMonth;

    public YearMonth getAcquisitionYearMonth() {
        return YearMonth.parse(this.acquisitionYearMonth);
    }
}
