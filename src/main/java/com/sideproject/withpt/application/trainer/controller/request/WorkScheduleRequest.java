package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.single.WorkScheduleDto;
import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
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
public class WorkScheduleRequest {

    @ValidEnum(regexp = "MON|TUE|WED|THU|FRI|SAT|SUN", enumClass = Day.class)
    private Day day;

    private LocalTime inTime;
    private LocalTime outTime;

    public WorkScheduleDto toWorkScheduleDto() {
        return WorkScheduleDto.builder()
            .day(this.day)
            .inTime(this.inTime)
            .outTime(this.outTime)
            .build();
    }

    public static List<WorkScheduleDto> toWorkScheduleDtos(List<WorkScheduleRequest> workSchedules) {
        return workSchedules.stream()
            .map(WorkScheduleRequest::toWorkScheduleDto)
            .collect(Collectors.toList());
    }
}
