package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.domain.trainer.Award;
import java.time.Year;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AwardDto {

    private final String name;
    private final String institution;
    private final Year acquisitionYear;

    @Builder
    private AwardDto(String name, String institution, Year acquisitionYear) {
        this.name = name;
        this.institution = institution;
        this.acquisitionYear = acquisitionYear;
    }

    public Award toEntity() {
        return Award.builder()
            .name(this.name)
            .institution(this.institution)
            .acquisitionYear(this.acquisitionYear)
            .build();
    }

}
