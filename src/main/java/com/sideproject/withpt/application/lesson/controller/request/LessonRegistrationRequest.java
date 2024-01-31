package com.sideproject.withpt.application.lesson.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.Lesson;
import com.sideproject.withpt.domain.pt.LessonSchedule;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonRegistrationRequest {

    @NotNull(message = "등록 요청 ID는 필수입니다.")
    private Long registrationRequestId;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    @ValidEnum(regexp = "MON|TUE|WED|THU|FRI|SAT|SUN", enumClass = Day.class)
    private Day weekday;

    @NotNull
    private LocalTime time;

    public Lesson toEntity(Member member, Trainer trainer, Gym gym, String loginRole) {
        LessonSchedule firstRegisteredLesson = LessonSchedule.builder()
            .date(this.date)
            .time(this.time)
            .weekday(this.weekday).build();

        return Lesson.builder()
            .member(member)
            .trainer(trainer)
            .gym(gym)
            .schedule(firstRegisteredLesson)
            .status(
                loginRole.equals(Role.TRAINER.name()) ? LessonStatus.RESERVED : LessonStatus.PENDING_APPROVAL
            )
            .registeredBy(loginRole)
            .build();
    }
}
