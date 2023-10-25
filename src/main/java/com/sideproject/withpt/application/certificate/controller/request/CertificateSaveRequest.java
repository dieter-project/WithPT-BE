package com.sideproject.withpt.application.certificate.controller.request;

import com.sideproject.withpt.common.exception.validator.YearMonthType;
import com.sideproject.withpt.domain.trainer.Certificate;
import java.time.YearMonth;
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
public class CertificateSaveRequest {

    @NotBlank(message = "자격증명을 입력해주세요")
    private String name;

    @YearMonthType
    private String acquisitionYearMonth;

    public YearMonth getAcquisitionYearMonth() {
        return YearMonth.parse(this.acquisitionYearMonth);
    }

    public Certificate toEntity() {
        return Certificate.builder()
            .name(this.name)
            .acquisitionYearMonth(this.getAcquisitionYearMonth())
            .build();
    }
}
