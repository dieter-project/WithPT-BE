package com.sideproject.withpt.application.schedule.controller.request;

import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.time.LocalTime;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleEditRequest {

    @Valid
    private List<Schedule> workSchedules;

    @Getter
    @ToString
    public static class Schedule {

        private Long id;

        @ValidEnum(regexp = "MON|TUE|WED|THU|FRI|SAT|SUN", enumClass = Day.class)
        private Day weekday;

        private LocalTime inTime;

        private LocalTime outTime;

        @Builder
        private Schedule(Long id, Day weekday, LocalTime inTime, LocalTime outTime) {
            this.id = id;
            this.weekday = weekday;
            this.inTime = inTime;
            this.outTime = outTime;
        }

        public WorkSchedule toEntity(GymTrainer gymTrainer) {
            return WorkSchedule.builder()
                .gymTrainer(gymTrainer)
                .weekday(this.weekday)
                .inTime(this.inTime)
                .outTime(this.outTime)
                .build();
        }
    }

}
