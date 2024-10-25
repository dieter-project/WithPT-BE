package com.sideproject.withpt.application.lesson.service.response;

import com.sideproject.withpt.application.gym.service.response.GymResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LessonInfoResponse {

    private LessonResponse lesson;
    private GymResponse gym;

    @Builder
    private LessonInfoResponse(LessonResponse lesson, GymResponse gym) {
        this.lesson = lesson;
        this.gym = gym;
    }
}
