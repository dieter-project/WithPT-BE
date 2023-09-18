package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkScheduleDto {

    private Day day;
    private LocalTime inTime;
    private LocalTime outTime;

    public WorkSchedule toEntity() {
        return WorkSchedule.builder()
            .weekday(this.day)
            .inTime(this.inTime)
            .outTime(this.outTime)
            .build();
    }

    public static List<WorkSchedule> toEntities(List<WorkScheduleDto> workScheduleDtos) {
        return workScheduleDtos.stream()
            .map(WorkScheduleDto::toEntity)
            .collect(Collectors.toList());
    }
}
