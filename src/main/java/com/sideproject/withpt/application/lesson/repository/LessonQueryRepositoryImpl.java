package com.sideproject.withpt.application.lesson.repository;

import static com.sideproject.withpt.domain.gym.QGym.gym;
import static com.sideproject.withpt.domain.member.QMember.member;
import static com.sideproject.withpt.domain.pt.QLesson.lesson;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.lesson.repository.dto.LessonInfoResponse;
import com.sideproject.withpt.application.lesson.repository.dto.QLessonInfoResponse;
import com.sideproject.withpt.application.lesson.repository.dto.QLessonInfoResponse_Gym;
import com.sideproject.withpt.application.lesson.repository.dto.QLessonInfoResponse_Member;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.pt.Lesson;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LessonQueryRepositoryImpl implements LessonQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Lesson> findByGymTrainerAndDateAndTime(GymTrainer gymTrainer, LocalDate date, LocalTime time) {
        return Optional.ofNullable(jpaQueryFactory
            .selectFrom(lesson)
            .where(
                lesson.gymTrainer.eq(gymTrainer),
                lesson.schedule.date.eq(date),
                lesson.schedule.time.eq(time)
            ).fetchOne()
        );
    }

    @Override
    public List<Lesson> getBookedLessonBy(GymTrainer gymTrainer, LocalDate date) {
        return jpaQueryFactory
            .selectFrom(lesson)
            .where(
                lesson.gymTrainer.eq(gymTrainer),
                lesson.schedule.date.eq(date),
                lesson.status.eq(LessonStatus.RESERVED).or(lesson.status.eq(LessonStatus.PENDING_APPROVAL))
            ).fetch();
    }

    @Override
    public List<LessonInfoResponse> getLessonScheduleMembers(Long trainerId, Long gymId, LocalDate date, LessonStatus status) {
        return jpaQueryFactory
            .select(
                new QLessonInfoResponse(
                    lesson.id,
                    lesson.schedule,
                    lesson.beforeSchedule,
                    lesson.status,
                    lesson.requester,
                    lesson.receiver,
                    lesson.registeredBy,
                    lesson.modifiedBy,
                    new QLessonInfoResponse_Member(
                        lesson.member.id,
                        lesson.member.name
                    ),
                    new QLessonInfoResponse_Gym(
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

    @Override
    public LessonInfoResponse findLessonScheduleInfoBy(Long lessonId) {
        return jpaQueryFactory
            .select(
                new QLessonInfoResponse(
                    lesson.id,
                    lesson.schedule,
                    lesson.beforeSchedule,
                    lesson.status,
                    lesson.requester,
                    lesson.receiver,
                    lesson.registeredBy,
                    lesson.modifiedBy,
                    new QLessonInfoResponse_Member(
                        member.id,
                        member.name
                    ),
                    new QLessonInfoResponse_Gym(
                        gym.id,
                        gym.name
                    )
                )
            )
            .from(lesson)
            .join(lesson.member, member)
            .join(lesson.gymTrainer.gym, gym)
            .where(
                lesson.id.eq(lessonId)
            )
            .fetchOne();
    }

    @Override
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
