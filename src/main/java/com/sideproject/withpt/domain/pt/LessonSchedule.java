package com.sideproject.withpt.domain.pt;

import com.sideproject.withpt.application.type.Day;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LessonSchedule {

    private LocalDate date;

    @Column(name = "TIME", columnDefinition = "TIME")
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"DAY\"")
    private Day weekday;

    public void changeSchedule(LessonSchedule schedule) {
        this.date = schedule.getDate();
        this.time = schedule.getTime();
        this.weekday = schedule.getWeekday();
    }
}
