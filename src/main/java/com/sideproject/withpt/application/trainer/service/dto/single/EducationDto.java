package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.domain.trainer.Education;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EducationDto {

    private String name;
    private YearMonth acquisitionYearMonth;

    public Education toEntity() {
        return Education.builder()
            .name(this.name)
            .acquisitionYearMonth(this.acquisitionYearMonth)
            .build();
    }

    public static List<Education> toEntities(List<EducationDto> educationDtos) {
        return educationDtos.stream()
            .map(EducationDto::toEntity)
            .collect(Collectors.toList());
    }
}
