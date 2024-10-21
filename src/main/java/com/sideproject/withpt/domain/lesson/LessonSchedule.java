package com.sideproject.withpt.domain.lesson;

import com.sideproject.withpt.common.type.Day;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@Embeddable
public class LessonSchedule {

    private LocalDate date;

    @Column(name = "TIME", columnDefinition = "TIME")
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"DAY\"")
    private Day weekday;

    @Builder
    private LessonSchedule(LocalDate date, LocalTime time, Day weekday) {
        this.date = date;
        this.time = time;
        this.weekday = weekday;
    }

    public void changeSchedule(LessonSchedule schedule) {
        this.date = schedule.getDate();
        this.time = schedule.getTime();
        this.weekday = schedule.getWeekday();
    }
}
