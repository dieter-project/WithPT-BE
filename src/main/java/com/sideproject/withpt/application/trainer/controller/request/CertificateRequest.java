package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.single.CertificateDto;
import com.sideproject.withpt.common.exception.validator.YearMonthType;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CertificateRequest {

    @NotBlank(message = "자격증명을 입력해주세요")
    private String name;

    @YearMonthType
    private String acquisitionYearMonth;

    public CertificateDto toCertificateDto() {
        return CertificateDto.builder()
            .name(this.name)
            .acquisitionYearMonth(YearMonth.parse(this.acquisitionYearMonth))
            .build();
    }

    public static List<CertificateDto> toCertificateDtos(List<CertificateRequest> certificates) {
        return certificates.stream()
            .map(CertificateRequest::toCertificateDto)
            .collect(Collectors.toList());
    }
}
