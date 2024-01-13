package com.sideproject.withpt.application.schedule.controller.response;

import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.EmploymentStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
