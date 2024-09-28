package com.sideproject.withpt.application.trainer.service.dto.single;

import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WorkScheduleDto {

    private final Day day;
    private final LocalTime inTime;
    private final LocalTime outTime;

    @Builder
    private WorkScheduleDto(Day day, LocalTime inTime, LocalTime outTime) {
        this.day = day;
        this.inTime = inTime;
        this.outTime = outTime;
    }

    public WorkSchedule toEntity(GymTrainer gymTrainer) {
        return WorkSchedule.builder()
            .gymTrainer(gymTrainer)
            .weekday(this.day)
            .inTime(this.inTime)
            .outTime(this.outTime)
            .build();
    }

    public WorkSchedule toEntity(Gym gym) {
        return WorkSchedule.builder()
            .gym(gym)
            .weekday(this.day)
            .inTime(this.inTime)
            .outTime(this.outTime)
            .build();
    }

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
