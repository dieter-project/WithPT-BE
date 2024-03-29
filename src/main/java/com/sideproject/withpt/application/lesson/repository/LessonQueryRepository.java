package com.sideproject.withpt.application.lesson.repository;

import static com.sideproject.withpt.domain.pt.QLesson.lesson;
import static com.sideproject.withpt.domain.trainer.QWorkSchedule.workSchedule;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.lesson.controller.response.LessonInfo;
import com.sideproject.withpt.application.lesson.controller.response.QLessonInfo;
import com.sideproject.withpt.application.lesson.controller.response.QLessonInfo_Gym;
import com.sideproject.withpt.application.lesson.controller.response.QLessonInfo_Member;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.pt.Lesson;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LessonQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<Lesson> findLessonByTrainerAndDateAndTime(Trainer trainer, LocalDate date, LocalTime time) {
        return Optional.ofNullable(jpaQueryFactory
            .selectFrom(lesson)
            .where(
                lesson.trainer.eq(trainer),
                lesson.schedule.date.eq(date),
                lesson.schedule.time.eq(time)
            ).fetchOne()
        );
    }

    public Map<LocalTime, Boolean> getAvailableTrainerLessonSchedule(Long trainerId, Gym gym, Day weekday, LocalDate date) {
        WorkSchedule schedule = jpaQueryFactory
            .selectFrom(workSchedule)
            .where(
                workSchedule.trainer.id.eq(trainerId),
                workSchedule.gym.eq(gym),
                workSchedule.weekday.eq(weekday)
            ).fetchOne();

        LocalTime startTime = schedule.getInTime();
        LocalTime endTime = schedule.getOutTime();
        Duration interval = Duration.ofHours(1);

        List<LocalTime> times = jpaQueryFactory
            .select(lesson.schedule.time)
            .from(lesson)
            .where(
                lesson.trainer.id.eq(trainerId),
                lesson.schedule.date.eq(date),
                lesson.status.eq(LessonStatus.RESERVED)
            ).fetch();

        Map<LocalTime, Boolean> lessonSchedule = new LinkedHashMap<>();

        while (startTime.isBefore(endTime)) {
            lessonSchedule.put(startTime, times.contains(startTime));
            startTime = startTime.plus(interval);
        }

        log.info("{} 요일의 트레이너 일정 : {} ~ {}", weekday, startTime, endTime);
        log.info("예약된 시간 = {}", times);
        log.info("수업 스케줄 : {}", lessonSchedule);

        return lessonSchedule;
    }

    public List<LessonInfo> getLessonScheduleMembers(Long trainerId, Long gymId, LocalDate date, LessonStatus status) {
        return jpaQueryFactory
            .select(
                new QLessonInfo(
                    lesson.id,
                    lesson.schedule.date,
                    lesson.schedule.time,
                    lesson.status,
                    new QLessonInfo_Member(
                        lesson.member.id,
                        lesson.member.name
                    ),
                    new QLessonInfo_Gym(
                        lesson.gym.id,
                        lesson.gym.name
                    )
                )
            )
            .from(lesson)
            .join(lesson.member)
            .join(lesson.gym)
            .where(
                lesson.trainer.id.eq(trainerId),
                gymIdEq(gymId),
                scheduleDateEq(date),
                statusNotInOrEq(status)
            )
            .orderBy(
                lesson.schedule.time.asc(),
                lesson.gym.name.asc()
            )
            .fetch();
    }

    public LessonInfo getLessonSchedule(Long lessonId) {
        return jpaQueryFactory
            .select(
                new QLessonInfo(
                    lesson.id,
                    lesson.schedule.date,
                    lesson.schedule.time,
                    lesson.status,
                    new QLessonInfo_Member(
                        lesson.member.id,
                        lesson.member.name
                    ),
                    new QLessonInfo_Gym(
                        lesson.gym.id,
                        lesson.gym.name
                    )
                )
            )
            .from(lesson)
            .join(lesson.member)
            .join(lesson.gym)
            .where(
               lesson.id.eq(lessonId)
            )
            .fetchOne();
    }

    public List<LocalDate> getLessonScheduleOfMonth(Long trainerId, Long gymId, YearMonth date) {

        DateTemplate<String> localDateTemplate = Expressions.dateTemplate(
            String.class,
            "DATE_FORMAT({0}, {1})",
            lesson.schedule.date,
            ConstantImpl.create("%Y-%m")
        );

        return jpaQueryFactory
            .select(
                lesson.schedule.date
            )
            .from(lesson)
            .where(
                lesson.trainer.id.eq(trainerId),
                gymIdEq(gymId),
                localDateTemplate.eq(date.toString()),
                lesson.status.notIn(LessonStatus.CANCELED)
            ).distinct()
            .orderBy(lesson.schedule.date.asc())
            .fetch();
    }

    private BooleanExpression memberNameContains(String name) {
        return StringUtils.hasText(name) ? lesson.member.name.contains(name) : null;
    }

    private BooleanExpression trainerEq(Trainer trainer) {
        return ObjectUtils.isEmpty(trainer) ? null : lesson.trainer.eq(trainer);
    }

    private BooleanExpression gymEq(Gym gym) {
        return ObjectUtils.isEmpty(gym) ? null : lesson.gym.eq(gym);
    }

    private BooleanExpression gymIdEq(Long gymId) {
        return ObjectUtils.isEmpty(gymId) ? null : lesson.gym.id.eq(gymId);
    }

    private BooleanExpression statusNotInOrEq(LessonStatus status) {
        return ObjectUtils.isEmpty(status) ? lesson.status.notIn(LessonStatus.PENDING_APPROVAL) : lesson.status.eq(status);
    }

    private BooleanExpression scheduleDateEq(LocalDate date) {
        return ObjectUtils.isEmpty(date) ? null : lesson.schedule.date.eq(date);
    }

}
