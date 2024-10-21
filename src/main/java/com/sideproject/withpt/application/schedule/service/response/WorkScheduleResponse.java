package com.sideproject.withpt.application.schedule.service.response;

import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.domain.gym.WorkSchedule;
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
public class WorkScheduleResponse {

    private Long id;
    private Day weekday;
    private LocalTime inTime;
    private LocalTime outTime;

    public static WorkScheduleResponse of(WorkSchedule workSchedule) {
        return WorkScheduleResponse.builder()
            .id(workSchedule.getId())
            .weekday(workSchedule.getWeekday())
            .inTime(workSchedule.getInTime())
            .outTime(workSchedule.getOutTime())
            .build();
    }

    public static List<WorkScheduleResponse> of(List<WorkSchedule> workSchedules) {
        return workSchedules.stream()
            .map(WorkScheduleResponse::of)
            .collect(Collectors.toList());
    }

}
