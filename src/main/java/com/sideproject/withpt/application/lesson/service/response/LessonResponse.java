package com.sideproject.withpt.application.lesson.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sideproject.withpt.common.type.LessonStatus;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.lesson.LessonSchedule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class LessonResponse {

    private Long lessonId;
    private LessonSchedule schedule;
    private LessonSchedule beforeSchedule;
    private LessonStatus status;
    private String requester;
    private String receiver;
    private Role registeredBy;
    private Role modifiedBy;

    @Builder
    private LessonResponse(Long lessonId, LessonSchedule schedule, LessonSchedule beforeSchedule, LessonStatus status, String requester, String receiver, Role registeredBy, Role modifiedBy) {
        this.lessonId = lessonId;
        this.schedule = schedule;
        this.beforeSchedule = beforeSchedule;
        this.status = status;
        this.requester = requester;
        this.receiver = receiver;
        this.registeredBy = registeredBy;
        this.modifiedBy = modifiedBy;
    }


    public static LessonResponse of(Lesson lesson) {
        return LessonResponse.builder()
            .lessonId(lesson.getId())
            .schedule(lesson.getSchedule())
            .beforeSchedule(lesson.getBeforeSchedule())
            .status(lesson.getStatus())
            .requester(lesson.getRequester())
            .receiver(lesson.getReceiver())
            .registeredBy(lesson.getRegisteredBy())
            .modifiedBy(lesson.getModifiedBy())
            .build();
    }
}
