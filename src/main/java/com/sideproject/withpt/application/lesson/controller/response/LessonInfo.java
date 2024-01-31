package com.sideproject.withpt.application.lesson.controller.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.type.LessonStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LessonInfo {

    private Long lessonId;
    private LocalDate date;
    private LocalTime startTime;
    private LessonStatus status;
    private Member member;
    private Gym gym;

    @QueryProjection
    public LessonInfo(Long lessonId, LocalDate date, LocalTime startTime, LessonStatus status, Member member, Gym gym) {
        this.lessonId = lessonId;
        this.date = date;
        this.startTime = startTime;
        this.status = status;
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
