package com.sideproject.withpt.application.lesson.repository.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.domain.pt.LessonSchedule;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LessonInfoResponse {

    private Long lessonId;
    private LessonSchedule schedule;
    private LessonSchedule beforeSchedule;
    private LessonStatus status;
    private String requester;
    private String receiver;
    private Role registeredBy;
    private Role modifiedBy;
    private Member member;
    private Gym gym;

    @QueryProjection
    public LessonInfoResponse(Long lessonId, LessonSchedule schedule, LessonSchedule beforeSchedule, LessonStatus status, String requester, String receiver, Role registeredBy, Role modifiedBy, Member member, Gym gym) {
        this.lessonId = lessonId;
        this.schedule = schedule;
        this.beforeSchedule = beforeSchedule;
        this.status = status;
        this.requester = requester;
        this.receiver = receiver;
        this.registeredBy = registeredBy;
        this.modifiedBy = modifiedBy;
        this.member = member;
        this.gym = gym;
    }

    @Getter
    public static class Member {

        private final Long id;
        private final String name;

        @QueryProjection
        public Member(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Getter
    public static class Gym {

        private final Long id;
        private final String name;

        @QueryProjection
        public Gym(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
