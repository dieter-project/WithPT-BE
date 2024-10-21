package com.sideproject.withpt.application.lesson.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sideproject.withpt.common.type.LessonStatus;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.lesson.LessonSchedule;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PendingLessonInfo {

    private Long id;
    private LessonSchedule schedule;
    private LessonSchedule beforeSchedule;
    private LessonStatus status;
    private Role registeredBy;
    private Role modifiedBy;
    private MemberInfo member;
    private GymInfo gym;

    public static PendingLessonInfo from(Lesson lesson, Member member, Gym gym) {
        return PendingLessonInfo.builder()
            .id(lesson.getId())
            .schedule(lesson.getSchedule())
            .beforeSchedule(lesson.getBeforeSchedule())
            .registeredBy(lesson.getRegisteredBy())
            .modifiedBy(lesson.getModifiedBy())
            .member(new MemberInfo(member.getId(), member.getName()))
            .gym(new GymInfo(gym.getId(), gym.getName()))
            .build();
    }


    @Getter
    public static class MemberInfo {

        private final Long id;
        private final String name;

        public MemberInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Getter
    public static class GymInfo {

        private final Long id;
        private final String name;

        public GymInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
