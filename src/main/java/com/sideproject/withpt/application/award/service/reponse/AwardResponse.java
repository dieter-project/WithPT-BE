package com.sideproject.withpt.application.award.service.reponse;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.domain.user.trainer.Award;
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
    private YearMonth acquisitionYearMonth;

    @QueryProjection
    public AwardResponse(Long id, String name, String institution, YearMonth acquisitionYearMonth) {
        this.id = id;
        this.name = name;
        this.institution = institution;
        this.acquisitionYearMonth = acquisitionYearMonth;
    }

    public static AwardResponse of(Award award) {
        return AwardResponse.builder()
            .id(award.getId())
            .name(award.getName())
            .institution(award.getInstitution())
            .acquisitionYearMonth(award.getAcquisitionYearMonth())
            .build();
    }
}
