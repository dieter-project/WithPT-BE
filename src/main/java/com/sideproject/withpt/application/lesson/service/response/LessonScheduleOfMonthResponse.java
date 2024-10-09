package com.sideproject.withpt.application.lesson.service.response;

import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LessonScheduleOfMonthResponse {

    private List<String> filteringBy;
    private List<LocalDate> dates;

    public LessonScheduleOfMonthResponse(List<String> filteringBy, List<LocalDate> dates) {
        this.filteringBy = filteringBy;
        this.dates = dates;
    }
}
