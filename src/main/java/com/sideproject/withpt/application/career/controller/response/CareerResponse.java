package com.sideproject.withpt.application.career.controller.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.type.EmploymentStatus;
import com.sideproject.withpt.domain.trainer.Career;
import java.time.YearMonth;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor
public class CareerResponse {

    private Long id;
    private String centerName;
    private String jobPosition;
    private EmploymentStatus status;
    private YearMonth startOfWorkYearMonth;
    private YearMonth endOfWorkYearMonth;

    @QueryProjection
    public CareerResponse(Long id, String centerName, String jobPosition, EmploymentStatus status,
        YearMonth startOfWorkYearMonth, YearMonth endOfWorkYearMonth) {
        this.id = id;
        this.centerName = centerName;
        this.jobPosition = jobPosition;
        this.status = status;
        this.startOfWorkYearMonth = startOfWorkYearMonth;
        this.endOfWorkYearMonth = endOfWorkYearMonth;
    }

    public static CareerResponse of(Career career) {
        return CareerResponse.builder()
            .id(career.getId())
            .centerName(career.getCenterName())
            .jobPosition(career.getJobPosition())
            .status(career.getStatus())
            .startOfWorkYearMonth(career.getStartOfWorkYearMonth())
            .endOfWorkYearMonth(career.getEndOfWorkYearMonth())
            .build();
    }
}
