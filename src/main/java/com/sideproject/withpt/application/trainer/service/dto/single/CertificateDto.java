package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.domain.trainer.Certificate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CertificateDto {

    private String name;
    private String institution;
    private YearMonth acquisitionYearMonth;

    public Certificate toEntity() {
        return Certificate.builder()
            .name(this.name)
            .institution(this.institution)
            .acquisitionYearMonth(this.acquisitionYearMonth)
            .build();
    }

    public static List<Certificate> toEntities(List<CertificateDto> certificateDtos) {
        return certificateDtos.stream()
            .map(CertificateDto::toEntity)
            .collect(Collectors.toList());
    }
}
