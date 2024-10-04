package com.sideproject.withpt.application.lesson.service.response;

import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.domain.pt.Lesson;
import com.sideproject.withpt.domain.pt.LessonSchedule;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LessonRegistrationResponse {

    private Long from;
    private Long to;
    private LessonSchedule schedule;
    private LessonStatus status;
    private String registeredBy;

    @Builder
    private LessonRegistrationResponse(Long from, Long to, LessonSchedule schedule, LessonStatus status, String registeredBy) {
        this.from = from;
        this.to = to;
        this.schedule = schedule;
        this.status = status;
        this.registeredBy = registeredBy;
    }

    public static LessonRegistrationResponse of(Lesson lesson, Long registrationRequestId, Long registrationReceiverId) {
        return LessonRegistrationResponse.builder()
            .from(registrationRequestId)
            .to(registrationReceiverId)
            .schedule(lesson.getSchedule())
            .status(lesson.getStatus())
            .registeredBy(lesson.getRegisteredBy())
            .build();
    }
}
