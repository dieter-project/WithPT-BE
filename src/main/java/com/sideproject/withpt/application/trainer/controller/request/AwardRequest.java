package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.single.AwardDto;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class AwardRequest {

    private String name;
    private String institution;
    private Year acquisitionYear;

    public AwardDto toAwardDto() {
        return AwardDto.builder()
            .name(this.name)
            .institution(this.institution)
            .acquisitionYear(this.acquisitionYear)
            .build();
    }

    public static List<AwardDto> toAwardDtos(List<AwardRequest> awards) {
        return awards.stream()
            .map(AwardRequest::toAwardDto)
            .collect(Collectors.toList());
    }
}
