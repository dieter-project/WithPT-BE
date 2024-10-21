package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.domain.user.trainer.Education;
import java.time.YearMonth;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EducationDto {

    private final String name;
    private final String institution;
    private final YearMonth acquisitionYearMonth;

    @Builder
    private EducationDto(String name, String institution, YearMonth acquisitionYearMonth) {
        this.name = name;
        this.institution = institution;
        this.acquisitionYearMonth = acquisitionYearMonth;
    }

    public Education toEntity() {
        return Education.builder()
            .name(this.name)
            .institution(this.institution)
            .acquisitionYearMonth(this.acquisitionYearMonth)
            .build();
    }

}
