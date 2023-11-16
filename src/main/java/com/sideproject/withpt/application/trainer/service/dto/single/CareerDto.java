package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.application.type.EmploymentStatus;
import com.sideproject.withpt.domain.trainer.Career;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CareerDto {

    private String centerName;
    private String jobPosition;
    private EmploymentStatus status;
    private YearMonth startOfWorkYearMonth;
    private YearMonth endOfWorkYearMonth;

    public Career toEntity() {
        return Career.builder()
            .centerName(this.centerName)
            .jobPosition(this.jobPosition)
            .status(this.status)
            .startOfWorkYearMonth(this.startOfWorkYearMonth)
            .endOfWorkYearMonth(this.endOfWorkYearMonth)
            .build();
    }

    public static List<Career> toEntities(List<CareerDto> careerDtos) {
        return careerDtos.stream()
            .map(CareerDto::toEntity)
            .collect(Collectors.toList());
    }
}
