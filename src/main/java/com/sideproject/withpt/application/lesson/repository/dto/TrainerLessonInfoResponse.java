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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerLessonInfoResponse {

    private Lesson lesson;
    private Member member;
    private Gym gym;

    @QueryProjection
    public TrainerLessonInfoResponse(Lesson lesson, Member member, Gym gym) {
        this.lesson = lesson;
        this.member = member;
        this.gym = gym;
    }

    @Getter
    @JsonInclude(Include.NON_NULL)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Lesson {

        private Long id;
        private LessonSchedule schedule;
        private LessonSchedule beforeSchedule;
        private LessonStatus status;
        private String requester;
        private String receiver;
        private Role registeredBy;
        private Role modifiedBy;

        @QueryProjection
        public Lesson(Long id, LessonSchedule schedule, LessonSchedule beforeSchedule, LessonStatus status, String requester, String receiver, Role registeredBy, Role modifiedBy) {
            this.id = id;
            this.schedule = schedule;
            this.beforeSchedule = beforeSchedule;
            this.status = status;
            this.requester = requester;
            this.receiver = receiver;
            this.registeredBy = registeredBy;
            this.modifiedBy = modifiedBy;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Member {

        private Long id;
        private String name;

        @QueryProjection
        public Member(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Gym {

        private Long id;
        private String name;

        @QueryProjection
        public Gym(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
