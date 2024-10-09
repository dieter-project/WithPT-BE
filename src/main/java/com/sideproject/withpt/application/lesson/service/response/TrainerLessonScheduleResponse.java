package com.sideproject.withpt.application.lesson.service.response;

import com.sideproject.withpt.application.lesson.repository.dto.TrainerLessonInfoResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerLessonScheduleResponse {

    private List<TrainerLessonInfoResponse> lessonInfos;

    public TrainerLessonScheduleResponse(List<TrainerLessonInfoResponse> lessonInfos) {
        this.lessonInfos = lessonInfos;
    }
}
