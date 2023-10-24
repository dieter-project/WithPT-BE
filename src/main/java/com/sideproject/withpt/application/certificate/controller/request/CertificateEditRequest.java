package com.sideproject.withpt.application.certificate.controller.request;

import com.sideproject.withpt.common.exception.validator.YearMonthType;
import java.time.YearMonth;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CertificateEditRequest {

    @NotNull(message = "자격증 Id는 필수입니다.")
    private Long id;

    @NotBlank(message = "자격증명을 입력해주세요")
    private String name;

    @YearMonthType
    private String acquisitionYearMonth;

    public YearMonth getAcquisitionYearMonth() {
        return YearMonth.parse(this.acquisitionYearMonth);
    }
}
