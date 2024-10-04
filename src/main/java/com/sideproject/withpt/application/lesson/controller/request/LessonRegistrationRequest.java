package com.sideproject.withpt.application.lesson.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.Lesson;
import com.sideproject.withpt.domain.pt.LessonSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class LessonRegistrationRequest {

    @NotNull(message = "수업 등록을 요청하는 ID는 필수입니다.")
    private Long registrationRequestId;

    @NotNull(message = "수업 등록 요청을 받는 ID는 필수입니다.")
    private Long registrationReceiverId;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    @ValidEnum(regexp = "MON|TUE|WED|THU|FRI|SAT|SUN", enumClass = Day.class)
    private Day weekday;

    @NotNull
    private LocalTime time;

    @Builder
    private LessonRegistrationRequest(Long registrationRequestId, Long registrationReceiverId, LocalDate date, Day weekday, LocalTime time) {
        this.registrationRequestId = registrationRequestId;
        this.registrationReceiverId = registrationReceiverId;
        this.date = date;
        this.weekday = weekday;
        this.time = time;
    }

    public Lesson toEntity(Member member, GymTrainer gymTrainer, String loginRole) {
        LessonSchedule firstRegisteredLesson = LessonSchedule.builder()
            .date(this.date)
            .time(this.time)
            .weekday(this.weekday)
            .build();

        return Lesson.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .schedule(firstRegisteredLesson)
            .status(determineLessonStatusByRole(loginRole))
            .registeredBy(loginRole)
            .build();
    }

    private LessonStatus determineLessonStatusByRole(String loginRole) {
        return loginRole.equals(Role.TRAINER.name()) ? LessonStatus.RESERVED : LessonStatus.PENDING_APPROVAL;
    }
}
