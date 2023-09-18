package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.domain.trainer.Academic;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AcademicDto {

    private String name;
    private String major;
    private Year enrollmentYear;
    private Year graduationYear;

    public Academic toEntity() {
        return Academic.builder()
            .name(this.name)
            .major(this.major)
            .enrollmentYear(this.enrollmentYear)
            .graduationYear(this.graduationYear)
            .build();
    }

    public static List<Academic> toEntities(List<AcademicDto> academicDtos) {
        return academicDtos.stream()
            .map(AcademicDto::toEntity)
            .collect(Collectors.toList());
    }
}
