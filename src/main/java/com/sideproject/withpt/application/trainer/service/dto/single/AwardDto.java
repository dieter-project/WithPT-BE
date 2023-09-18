package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.domain.trainer.Award;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AwardDto {

    private String name;
    private String institution;
    private Year acquisitionYear;

    public Award toEntity() {
        return Award.builder()
            .name(this.name)
            .institution(this.institution)
            .acquisitionYear(this.acquisitionYear)
            .build();
    }

    public static List<Award> toEntities(List<AwardDto> awardDtos) {
        return awardDtos.stream()
            .map(AwardDto::toEntity)
            .collect(Collectors.toList());
    }
}
