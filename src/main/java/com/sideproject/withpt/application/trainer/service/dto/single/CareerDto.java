package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.common.type.EmploymentStatus;
import com.sideproject.withpt.domain.trainer.Career;
import java.time.YearMonth;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CareerDto {

    private final String centerName;
    private final String jobPosition;
    private final EmploymentStatus status;
    private final YearMonth startOfWorkYearMonth;
    private final YearMonth endOfWorkYearMonth;

    @Builder
    private CareerDto(String centerName, String jobPosition, EmploymentStatus status, YearMonth startOfWorkYearMonth, YearMonth endOfWorkYearMonth) {
        this.centerName = centerName;
        this.jobPosition = jobPosition;
        this.status = status;
        this.startOfWorkYearMonth = startOfWorkYearMonth;
        this.endOfWorkYearMonth = endOfWorkYearMonth;
    }

    public Career toEntity() {
        return Career.builder()
            .centerName(this.centerName)
            .jobPosition(this.jobPosition)
            .status(this.status)
            .startOfWorkYearMonth(this.startOfWorkYearMonth)
            .endOfWorkYearMonth(this.endOfWorkYearMonth)
            .build();
    }
}
