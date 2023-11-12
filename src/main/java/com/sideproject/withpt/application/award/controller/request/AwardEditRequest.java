package com.sideproject.withpt.application.award.controller.request;

import com.sideproject.withpt.common.exception.validator.YearMonthType;
import com.sideproject.withpt.common.exception.validator.YearType;
import java.time.Year;
import java.time.YearMonth;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AwardEditRequest {

    @NotNull(message = "자격증 Id는 필수입니다.")
    private Long id;

    @NotBlank(message = "수상명을 입력해주세요")
    private String name;

    @NotBlank(message = "기관명을 입력해주세요")
    private String institution;

    @YearType
    private String acquisitionYear;

    public Year getAcquisitionYear() {
        return Year.parse(this.acquisitionYear);
    }
}
