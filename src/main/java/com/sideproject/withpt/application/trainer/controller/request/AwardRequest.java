package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.single.AwardDto;
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
public class AwardRequest {

    @NotBlank(message = "수상 명을 입력해주세요")
    private String name;

    @NotBlank(message = "기관명을 입력해주세요")
    private String institution;

    @YearMonthType
    private String acquisitionYearMonth;

    public AwardDto toAwardDto() {
        return AwardDto.builder()
            .name(this.name)
            .institution(this.institution)
            .acquisitionYearMonth(YearMonth.parse(this.acquisitionYearMonth))
            .build();
    }

    public static List<AwardDto> toAwardDtos(List<AwardRequest> awards) {
        return awards.stream()
            .map(AwardRequest::toAwardDto)
            .collect(Collectors.toList());
    }
}
