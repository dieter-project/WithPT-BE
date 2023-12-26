package com.sideproject.withpt.application.lesson.repository;

import static com.sideproject.withpt.domain.pt.QLesson.*;
import static com.sideproject.withpt.domain.pt.QPersonalTraining.*;
import static com.sideproject.withpt.domain.trainer.QWorkSchedule.*;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.lesson.controller.response.AvailableLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.controller.response.QSearchMemberResponse;
import com.sideproject.withpt.application.lesson.controller.response.SearchMemberResponse;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.application.type.PTInfoInputStatus;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.pt.Lesson;
import com.sideproject.withpt.domain.pt.QLesson;
import com.sideproject.withpt.domain.pt.QPersonalTraining;
import com.sideproject.withpt.domain.trainer.QWorkSchedule;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LessonQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Slice<SearchMemberResponse> findAllMembersByGymIdAndName(Trainer trainer, Gym gym, String name,
        Pageable pageable) {

        NumberExpression<Integer> remainingPtCountRank = new CaseBuilder()
            .when(personalTraining.remainingPtCount.gt(0)).then(1)
            .otherwise(2);

        List<SearchMemberResponse> content = jpaQueryFactory
            .select(
                new QSearchMemberResponse(
                    personalTraining.member.id,
                    personalTraining.member.name,
                    personalTraining.member.authentication.birth,
                    personalTraining.member.authentication.sex,
                    personalTraining.remainingPtCount
                )
            )
            .from(personalTraining)
            .leftJoin(personalTraining.member)
            .where(
                trainerEq(trainer),
                gymEq(gym),
                personalTraining.registrationAllowedStatus.eq(PtRegistrationAllowedStatus.APPROVED),
                personalTraining.infoInputStatus.eq(PTInfoInputStatus.INFO_REGISTERED),
                memberNameContains(name)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(
                remainingPtCountRank.asc(),
                personalTraining.member.name.asc()
            )
            .fetch();

        boolean hasNext = false;

        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    public boolean existsLessonByTrainer(Trainer trainer, LocalDate date, LocalTime time,
        LessonStatus status) {
        return !ObjectUtils.isEmpty(
            jpaQueryFactory
                .select(lesson.id)
                .from(lesson)
                .where(
                    lesson.personalTraining.id.in(
                        JPAExpressions
                            .select(personalTraining.id)
                            .from(personalTraining)
                            .where(
                                personalTraining.trainer.eq(trainer)
                            )
                    ),
                    lesson.date.eq(date),
                    lesson.time.eq(time),
                    lesson.status.eq(status)
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
            .select(lesson.time)
            .from(lesson)
            .where(
                lesson.personalTraining.id.in(
                    JPAExpressions
                        .select(personalTraining.id)
                        .from(personalTraining)
                        .where(
                            personalTraining.trainer.id.eq(trainerId)
                        )
                ),
                lesson.date.eq(date),
                lesson.status.eq(LessonStatus.RESERVATION)
            ).fetch();

        Map<LocalTime, Boolean> lessonSchedule = new LinkedHashMap<>();

        while(startTime.isBefore(endTime)) {
            lessonSchedule.put(startTime, times.contains(startTime));
            startTime = startTime.plus(interval);
        }

        log.info("{} 요일의 트레이너 일정 : {} ~ {}", weekday, startTime, endTime);
        log.info("예약된 시간 = {}", times);
        log.info("수업 스케줄 : {}", lessonSchedule);

        return lessonSchedule;
    }

    private BooleanExpression memberNameContains(String name) {
        return StringUtils.hasText(name) ? personalTraining.member.name.contains(name) : null;
    }

    private BooleanExpression trainerEq(Trainer trainer) {
        return ObjectUtils.isEmpty(trainer) ? null : personalTraining.trainer.eq(trainer);
    }

    private BooleanExpression gymEq(Gym gym) {
        return ObjectUtils.isEmpty(gym) ? null : personalTraining.gym.eq(gym);
    }
}
