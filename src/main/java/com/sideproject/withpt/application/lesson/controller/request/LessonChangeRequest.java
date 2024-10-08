package com.sideproject.withpt.application.lesson.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class LessonChangeRequest {

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    @ValidEnum(regexp = "MON|TUE|WED|THU|FRI|SAT|SUN", enumClass = Day.class)
    private Day weekday;

    @NotNull
    private LocalTime time;

    @Builder
    private LessonChangeRequest(LocalDate date, Day weekday, LocalTime time) {
        this.date = date;
        this.weekday = weekday;
        this.time = time;
    }
}
