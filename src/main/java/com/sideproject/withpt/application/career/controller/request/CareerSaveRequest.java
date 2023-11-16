package com.sideproject.withpt.application.career.controller.request;

import com.sideproject.withpt.application.type.EmploymentStatus;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.exception.validator.YearMonthType;
import com.sideproject.withpt.domain.trainer.Career;
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
public class CareerSaveRequest {

    @NotBlank(message = "센터명을 입력해주세요")
    private String centerName;

    @NotBlank(message = "직책을 입력해주세요")
    private String jobPosition;

    @ValidEnum(regexp = " EMPLOYED|UNEMPLOYED", enumClass = EmploymentStatus.class)
    private EmploymentStatus status;

    @YearMonthType
    private String startOfWorkYearMonth;

    @YearMonthType
    private String endOfWorkYearMonth;

    public YearMonth getStartOfWorkYearMonth() {
        return YearMonth.parse(this.startOfWorkYearMonth);
    }

    public YearMonth getEndOfWorkYearMonth() {
        return YearMonth.parse(this.endOfWorkYearMonth);
    }

    public Career toEntity() {
        return Career.builder()
            .centerName(this.centerName)
            .jobPosition(this.jobPosition)
            .status(this.status)
            .startOfWorkYearMonth(this.getStartOfWorkYearMonth())
            .endOfWorkYearMonth(this.getEndOfWorkYearMonth())
            .build();
    }
}
