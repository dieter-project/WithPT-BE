package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.single.AcademicDto;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class AcademicRequest {

    private String name;
    private String major;
    private Year enrollmentYear;
    private Year graduationYear;

    public AcademicDto toAcademicDto() {
        return AcademicDto.builder()
            .name(this.name)
            .major(this.major)
            .enrollmentYear(this.enrollmentYear)
            .graduationYear(this.graduationYear)
            .build();
    }

    public static List<AcademicDto> toAcademicDtos(List<AcademicRequest> academics) {
        return academics.stream()
            .map(AcademicRequest::toAcademicDto)
            .collect(Collectors.toList());
    }
}
