package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.single.WorkScheduleDto;
import com.sideproject.withpt.application.type.Day;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class WorkScheduleRequest {

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
