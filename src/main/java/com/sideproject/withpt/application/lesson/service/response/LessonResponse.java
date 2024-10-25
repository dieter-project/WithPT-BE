package com.sideproject.withpt.application.lesson.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sideproject.withpt.common.type.LessonStatus;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.lesson.LessonSchedule;
import com.sideproject.withpt.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class LessonResponse {

    private Long id;
    private LessonSchedule schedule;
    private LessonSchedule beforeSchedule;
    private LessonStatus status;
    private LessonUserResponse requester;
    private LessonUserResponse receiver;
    private Role registeredBy;
    private Role modifiedBy;

    @Builder
    private LessonResponse(Long id, LessonSchedule schedule, LessonSchedule beforeSchedule, LessonStatus status, LessonUserResponse requester, LessonUserResponse receiver, Role registeredBy, Role modifiedBy) {
        this.id = id;
        this.schedule = schedule;
        this.beforeSchedule = beforeSchedule;
        this.status = status;
        this.requester = requester;
        this.receiver = receiver;
        this.registeredBy = registeredBy;
        this.modifiedBy = modifiedBy;
    }

    public static LessonResponse of(Lesson lesson) {
        User requester = lesson.getRequester();
        User receiver = lesson.getReceiver();

        return LessonResponse.builder()
            .id(lesson.getId())
            .schedule(lesson.getSchedule())
            .beforeSchedule(lesson.getBeforeSchedule())
            .status(lesson.getStatus())
            .requester(LessonUserResponse.of(requester))
            .receiver(LessonUserResponse.of(receiver))
            .registeredBy(lesson.getRegisteredBy())
            .modifiedBy(lesson.getModifiedBy())
            .build();
    }
}
