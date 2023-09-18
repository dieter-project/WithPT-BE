package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.single.CareerDto;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class CareerRequest {

    private String centerName;
    private YearMonth startOfWorkYearMonth;
    private YearMonth endOfWorkYearMonth;

    public CareerDto toCareerDto() {
        return CareerDto.builder()
            .centerName(this.centerName)
            .startOfWorkYearMonth(this.startOfWorkYearMonth)
            .endOfWorkYearMonth(this.endOfWorkYearMonth)
            .build();
    }

    public static List<CareerDto> toCareerDtos(List<CareerRequest> careers) {
        return careers.stream()
            .map(CareerRequest::toCareerDto)
            .collect(Collectors.toList());
    }
}
