package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.model.single.AcademicDto;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.exception.validator.YearMonthType;
import com.sideproject.withpt.common.type.AcademicInstitution;
import com.sideproject.withpt.common.type.Degree;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AcademicRequest {

    @ValidEnum(enumClass = AcademicInstitution.class)
    private AcademicInstitution institution;

    @NotBlank(message = "학력명을 입력해주세요")
    private String name;

    private String major;

    @Default
    @ValidEnum(regexp = "HIGH_SCHOOL_DIPLOMA|ASSOCIATE|BACHELOR|MASTER|DOCTORATE", enumClass = Degree.class)
    private Degree degree = Degree.HIGH_SCHOOL_DIPLOMA;

    @Default
    private String country = "Korea";

    @YearMonthType
    private String enrollmentYearMonth;

    @YearMonthType
    private String graduationYearMonth;

    public AcademicDto toAcademicDto() {
        return AcademicDto.builder()
            .name(this.name)
            .major(this.major)
            .institution(this.institution)
            .degree(this.degree)
            .country(this.country)
            .enrollmentYearMonth(YearMonth.parse(this.enrollmentYearMonth))
            .graduationYearMonth(YearMonth.parse(this.graduationYearMonth))
            .build();
    }

    public static List<AcademicDto> toAcademicDtos(List<AcademicRequest> academics) {
        return academics.stream()
            .map(AcademicRequest::toAcademicDto)
            .collect(Collectors.toList());
    }
}
