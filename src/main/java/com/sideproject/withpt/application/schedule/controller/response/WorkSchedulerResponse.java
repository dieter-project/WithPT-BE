package com.sideproject.withpt.application.schedule.controller.response;

import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkSchedulerResponse {

    private Long id;
    private Day weekday;
    private LocalTime inTime;
    private LocalTime outTime;

    public static WorkSchedulerResponse of(WorkSchedule workSchedule) {
        return WorkSchedulerResponse.builder()
            .id(workSchedule.getId())
            .weekday(workSchedule.getWeekday())
            .inTime(workSchedule.getInTime())
            .outTime(workSchedule.getOutTime())
            .build();
    }

    public static List<WorkSchedulerResponse> of(List<WorkSchedule> workSchedules) {
        return workSchedules.stream()
            .map(WorkSchedulerResponse::of)
            .collect(Collectors.toList());
    }

}
