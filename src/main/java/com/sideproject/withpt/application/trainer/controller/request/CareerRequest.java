package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.single.CareerDto;
import com.sideproject.withpt.common.exception.validator.YearMonthType;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
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
public class CareerRequest {

    @NotBlank(message = "센터명을 입력해주세요")
    private String centerName;

    @NotBlank(message = "직책을 입력해주세요")
    private String jobPosition;

    @YearMonthType
    private String startOfWorkYearMonth;

    @YearMonthType
    private String endOfWorkYearMonth;

    public CareerDto toCareerDto() {
        return CareerDto.builder()
            .centerName(this.centerName)
            .jobPosition(this.jobPosition)
            .startOfWorkYearMonth(YearMonth.parse(this.startOfWorkYearMonth))
            .endOfWorkYearMonth(YearMonth.parse(this.endOfWorkYearMonth))
            .build();
    }

    public static List<CareerDto> toCareerDtos(List<CareerRequest> careers) {
        return careers.stream()
            .map(CareerRequest::toCareerDto)
            .collect(Collectors.toList());
    }
}
