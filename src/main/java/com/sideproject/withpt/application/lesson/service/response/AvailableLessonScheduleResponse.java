package com.sideproject.withpt.application.lesson.service.response;

import com.sideproject.withpt.common.type.Day;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
    private List<LessonTime> schedules;

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class LessonTime {

        private LocalTime time;
        private boolean isBooked;

        public static LessonTime of(LocalTime time, boolean isBooked) {
            return new LessonTime(time, isBooked);
        }
    }

    public static AvailableLessonScheduleResponse of(Long trainerId, Long gymId, LocalDate date, Day day, List<LessonTime> times) {
        return AvailableLessonScheduleResponse.builder()
            .trainerId(trainerId)
            .gymId(gymId)
            .date(date)
            .day(day)
            .schedules(times)
            .build();
    }
}
