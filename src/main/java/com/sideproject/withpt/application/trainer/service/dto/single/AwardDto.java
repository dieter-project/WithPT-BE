package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.domain.trainer.Award;
import java.time.Year;
import java.time.YearMonth;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AwardDto {

    private final String name;
    private final String institution;
    private final YearMonth acquisitionYearMonth;

    @Builder
    private AwardDto(String name, String institution, YearMonth acquisitionYearMonth) {
        this.name = name;
        this.institution = institution;
        this.acquisitionYearMonth = acquisitionYearMonth;
    }

    public Award toEntity() {
        return Award.builder()
            .name(this.name)
            .institution(this.institution)
            .acquisitionYearMonth(this.acquisitionYearMonth)
            .build();
    }

}
