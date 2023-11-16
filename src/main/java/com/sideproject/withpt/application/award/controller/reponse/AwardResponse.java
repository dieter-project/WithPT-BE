package com.sideproject.withpt.application.award.controller.reponse;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.domain.trainer.Award;
import com.sideproject.withpt.domain.trainer.Certificate;
import java.time.Year;
import java.time.YearMonth;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
public class AwardResponse {

    private Long id;
    private String name;
    private String institution;
    private Year acquisitionYear;

    @QueryProjection
    public AwardResponse(Long id, String name, String institution, Year acquisitionYear) {
        this.id = id;
        this.name = name;
        this.institution = institution;
        this.acquisitionYear = acquisitionYear;
    }

    public static AwardResponse of(Award award) {
        return AwardResponse.builder()
            .id(award.getId())
            .name(award.getName())
            .institution(award.getInstitution())
            .acquisitionYear(award.getAcquisitionYear())
            .build();
    }
}
