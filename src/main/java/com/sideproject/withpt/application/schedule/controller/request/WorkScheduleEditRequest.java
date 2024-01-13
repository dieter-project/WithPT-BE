package com.sideproject.withpt.application.schedule.controller.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.time.LocalTime;
import java.util.List;
import javax.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@ToString
@NoArgsConstructor
public class WorkScheduleEditRequest {

    @Valid
    private List<Schedule> workSchedules;

    @Getter
    @ToString
    public static class Schedule {

        @Nullable
        private Long id;

        @ValidEnum(regexp = "MON|TUE|WED|THU|FRI|SAT|SUN", enumClass = Day.class)
        private Day weekday;

        private LocalTime inTime;

        private LocalTime outTime;

        public static WorkSchedule toEntity(Schedule schedule, Trainer trainer, Gym gym) {
            return WorkSchedule.builder()
                .trainer(trainer)
                .gym(gym)
                .weekday(schedule.getWeekday())
                .inTime(schedule.getInTime())
                .outTime(schedule.getOutTime())
                .build();
        }
    }

}
