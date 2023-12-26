package com.sideproject.withpt.application.lesson.controller.response;

import com.sideproject.withpt.application.type.Day;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailableLessonScheduleResponse {

    private Long trainerId;
    private Long gymId;
    private LocalDate date;
    private Day day;
    private Map<LocalTime, Boolean> lessonSchedule;

    public static AvailableLessonScheduleResponse of(Long trainerId, Long gymId, LocalDate date, Day day, Map<LocalTime, Boolean> lessonSchedule) {
        return AvailableLessonScheduleResponse.builder()
            .trainerId(trainerId)
            .gymId(gymId)
            .date(date)
            .day(day)
            .lessonSchedule(lessonSchedule)
            .build();
    }
}
