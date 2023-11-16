package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.application.type.AcademicInstitution;
import com.sideproject.withpt.application.type.Degree;
import com.sideproject.withpt.domain.trainer.Academic;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Builder
public class AcademicDto {

    private static final String NO_MAJOR = "전공 없음";

    private String name;
    private String major;
    private AcademicInstitution institution;
    private Degree degree;
    private String country;
    private Year enrollmentYear;
    private Year graduationYear;

    public Academic toEntity() {
        // 고등학교 -> 전공 없음
        // 학위 -> 고등학교 졸업장
        if(institution == AcademicInstitution.HIGH_SCHOOL) {
            this.major = NO_MAJOR;
            this.degree = Degree.HIGH_SCHOOL_DIPLOMA;
        }

        // 기관이 국내 대학이면 degree는 학사
        if(institution != AcademicInstitution.HIGH_SCHOOL && institution != AcademicInstitution.OVERSEAS_UNIVERSITY) {
            this.degree = Degree.BACHELOR;
        }

        return Academic.builder()
            .name(this.name)
            .major(this.major)
            .institution(this.institution)
            .degree(this.degree)
            .country(this.country)
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
