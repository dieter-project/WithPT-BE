package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.single.AcademicDto;
import com.sideproject.withpt.common.exception.validator.YearType;
import java.time.Year;
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
public class AcademicRequest {

    @NotBlank(message = "학력명을 입력해주세요")
    private String name;

    @NotBlank(message = "전공을 입력해주세요")
    private String major;

    @YearType
    private String enrollmentYear;

    @YearType
    private String graduationYear;

    public AcademicDto toAcademicDto() {
        return AcademicDto.builder()
            .name(this.name)
            .major(this.major)
            .enrollmentYear(Year.parse(this.enrollmentYear))
            .graduationYear(Year.parse(this.graduationYear))
            .build();
    }

    public static List<AcademicDto> toAcademicDtos(List<AcademicRequest> academics) {
        return academics.stream()
            .map(AcademicRequest::toAcademicDto)
            .collect(Collectors.toList());
    }
}
