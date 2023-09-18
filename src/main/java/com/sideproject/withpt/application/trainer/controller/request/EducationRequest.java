package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.single.EducationDto;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class EducationRequest {

    private String name;
    private YearMonth acquisitionYearMonth;

    public EducationDto toEducationDto() {
        return EducationDto.builder()
            .name(this.name)
            .acquisitionYearMonth(this.acquisitionYearMonth)
            .build();
    }

    public static List<EducationDto> toEducationDtos(List<EducationRequest> educations) {
        return educations.stream()
            .map(EducationRequest::toEducationDto)
            .collect(Collectors.toList());
    }
}
