package com.sideproject.withpt.application.award.controller.request;

import com.sideproject.withpt.common.exception.validator.YearMonthType;
import com.sideproject.withpt.domain.trainer.Award;
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
public class AwardSaveRequest {

    @NotBlank(message = "수상명을 입력해주세요")
    private String name;

    @NotBlank(message = "기관명을 입력해주세요")
    private String institution;

    @YearMonthType
    private String acquisitionYearMonth;

    public YearMonth getAcquisitionYearMonth() {
        return YearMonth.parse(this.acquisitionYearMonth);
    }

    public Award toEntity() {
        return Award.builder()
            .name(this.name)
            .institution(this.institution)
            .acquisitionYearMonth(this.getAcquisitionYearMonth())
            .build();
    }
}
