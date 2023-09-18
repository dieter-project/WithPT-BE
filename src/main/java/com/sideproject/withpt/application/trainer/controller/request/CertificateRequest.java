package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.single.CertificateDto;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class CertificateRequest {

    private String name;
    private YearMonth acquisitionYearMonth;

    public CertificateDto toCertificateDto() {
        return CertificateDto.builder()
            .name(this.name)
            .acquisitionYearMonth(this.acquisitionYearMonth)
            .build();
    }

    public static List<CertificateDto> toCertificateDtos(List<CertificateRequest> certificates) {
        return certificates.stream()
            .map(CertificateRequest::toCertificateDto)
            .collect(Collectors.toList());
    }
}
