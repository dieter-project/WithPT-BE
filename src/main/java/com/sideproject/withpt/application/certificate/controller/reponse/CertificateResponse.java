package com.sideproject.withpt.application.certificate.controller.reponse;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.domain.trainer.Certificate;
import java.time.YearMonth;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
public class CertificateResponse {

    private Long id;
    private String name;
    private String institution;
    private YearMonth acquisitionYearMonth;

    @QueryProjection
    public CertificateResponse(Long id, String name, String institution, YearMonth acquisitionYearMonth) {
        this.id = id;
        this.name = name;
        this.institution = institution;
        this.acquisitionYearMonth = acquisitionYearMonth;
    }

    public static CertificateResponse of(Certificate certificate) {
        return CertificateResponse.builder()
            .id(certificate.getId())
            .name(certificate.getName())
            .institution(certificate.getInstitution())
            .acquisitionYearMonth(certificate.getAcquisitionYearMonth())
            .build();
    }
}
