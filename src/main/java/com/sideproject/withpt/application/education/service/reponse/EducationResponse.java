package com.sideproject.withpt.application.education.service.reponse;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.domain.user.trainer.Education;
import java.time.YearMonth;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
public class EducationResponse {

    private Long id;
    private String name;
    private String institution;
    private YearMonth acquisitionYearMonth;

    @QueryProjection
    public EducationResponse(Long id, String name, String institution, YearMonth acquisitionYearMonth) {
        this.id = id;
        this.name = name;
        this.institution = institution;
        this.acquisitionYearMonth = acquisitionYearMonth;
    }

    public static EducationResponse of(Education education) {
        return EducationResponse.builder()
            .id(education.getId())
            .name(education.getName())
            .institution(education.getInstitution())
            .acquisitionYearMonth(education.getAcquisitionYearMonth())
            .build();
    }
}
