package com.sideproject.withpt.domain.lesson;

import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.common.type.LessonStatus;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.user.member.Member;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lesson extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_trainer_id")
    private GymTrainer gymTrainer;

    @Embedded
    private LessonSchedule schedule;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "date", column = @Column(name = "BEFORE_DATE")),
        @AttributeOverride(name = "time", column = @Column(name = "BEFORE_TIME")),
        @AttributeOverride(name = "weekday", column = @Column(name = "BEFORE_DAY"))
    })
    private LessonSchedule beforeSchedule;

    @Enumerated(EnumType.STRING)
    private LessonStatus status;

    private String requester;

    private String receiver;

    @Enumerated
    private Role registeredBy;

    @Enumerated
    private Role modifiedBy;

    @Builder
    private Lesson(Member member, GymTrainer gymTrainer, LessonSchedule schedule, LessonSchedule beforeSchedule, LessonStatus status, String requester, String receiver, Role registeredBy, Role modifiedBy) {
        this.member = member;
        this.gymTrainer = gymTrainer;
        this.schedule = schedule;
        this.beforeSchedule = beforeSchedule;
        this.status = status;
        this.requester = requester;
        this.receiver = receiver;
        this.registeredBy = registeredBy;
        this.modifiedBy = modifiedBy;
    }

    public void cancel(LessonStatus status) {
        this.status = status;
    }

    public void changeLessonSchedule(LocalDate date, LocalTime time, Day weekday, Role requestByRole) {
        this.beforeSchedule = LessonSchedule.builder()
            .date(this.schedule.getDate())
            .time(this.schedule.getTime())
            .weekday(this.schedule.getWeekday())
            .build();

        this.schedule = LessonSchedule.builder()
            .date(date)
            .time(time)
            .weekday(weekday)
            .build();
        this.status = LessonStatus.PENDING_APPROVAL;
        this.modifiedBy = requestByRole;
    }

    public static Lesson createNewLessonRegistration(Member member, GymTrainer gymTrainer, LocalDate date, LocalTime time, Day weekday, Role requestByRole, Long registrationRequestId, Long registrationReceiverId) {
        LessonSchedule firstRegisteredLesson = LessonSchedule.builder()
            .date(date)
            .time(time)
            .weekday(weekday)
            .build();

        String requester = getRequester(registrationRequestId, requestByRole);
        String receiver = getReceiver(registrationReceiverId, requestByRole);
        return Lesson.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .schedule(firstRegisteredLesson)
            .status(
                requestByRole == Role.TRAINER ? LessonStatus.RESERVED : LessonStatus.PENDING_APPROVAL
            )
            .requester(requester)
            .receiver(receiver)
            .registeredBy(requestByRole)
            .build();
    }

    public void registrationOrScheduleChangeAccept() {
        this.status = LessonStatus.RESERVED;
    }
    public static String getRequester(Long requestId, Role requestByRole) {
        return requestByRole == Role.MEMBER ? requestId + "_" + Role.MEMBER : requestId + "_" + Role.TRAINER;
    }

    public static String getReceiver(Long receiverId, Role requestByRole) {
        return requestByRole == Role.MEMBER ? receiverId + "_" + Role.TRAINER : receiverId + "_" + Role.MEMBER;
    }
}
