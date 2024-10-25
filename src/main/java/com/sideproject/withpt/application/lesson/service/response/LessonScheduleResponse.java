package com.sideproject.withpt.application.lesson.service.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LessonScheduleResponse {

    private List<LessonInfoResponse> lessonInfos;

    public LessonScheduleResponse(List<LessonInfoResponse> lessonInfos) {
        this.lessonInfos = lessonInfos;
    }
}
