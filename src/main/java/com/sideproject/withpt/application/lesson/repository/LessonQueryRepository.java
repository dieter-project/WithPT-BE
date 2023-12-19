package com.sideproject.withpt.application.lesson.repository;

import static com.sideproject.withpt.domain.pt.QLesson.*;
import static com.sideproject.withpt.domain.pt.QPersonalTraining.*;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.lesson.controller.response.QSearchMemberResponse;
import com.sideproject.withpt.application.lesson.controller.response.SearchMemberResponse;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.application.type.PTInfoInputStatus;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.pt.Lesson;
import com.sideproject.withpt.domain.pt.QLesson;
import com.sideproject.withpt.domain.pt.QPersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
