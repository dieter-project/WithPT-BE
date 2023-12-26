package com.sideproject.withpt.application.lesson.controller.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.domain.gym.Gym;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Slice;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LessonMembersInGymResponse {

    private Long gymId;
    private String gymName;
    private LocalDate date;
    private Long totalCount;
    private Slice<LessonMember> lessonMembers;

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LessonMember {
        private Long memberId;
        private String memberName;
        private LocalTime startTime;
        private LocalTime endTime;

        @QueryProjection
        public LessonMember(Long memberId, String memberName, LocalTime startTime, LocalTime endTime) {
            this.memberId = memberId;
            this.memberName = memberName;
            this.startTime = startTime;
            this.endTime = endTime.plusMinutes(50);
        }
    }

    public static LessonMembersInGymResponse of(Gym gym, LocalDate date, Long totalCount, Slice<LessonMember> lessonMembers) {
        return LessonMembersInGymResponse.builder()
            .gymId(gym.getId())
            .gymName(gym.getName())
            .date(date)
            .totalCount(totalCount)
            .lessonMembers(lessonMembers)
            .build();
    }
}
