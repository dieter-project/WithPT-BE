package com.sideproject.withpt.application.lesson.repository;

import static com.sideproject.withpt.domain.gym.QGym.gym;
import static com.sideproject.withpt.domain.gym.QGymTrainer.gymTrainer;
import static com.sideproject.withpt.domain.member.QMember.member;
import static com.sideproject.withpt.domain.pt.QLesson.lesson;
import static com.sideproject.withpt.domain.trainer.QTrainer.trainer;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.lesson.repository.dto.MemberLessonInfoResponse;
import com.sideproject.withpt.application.lesson.repository.dto.QMemberLessonInfoResponse;
import com.sideproject.withpt.application.lesson.repository.dto.QMemberLessonInfoResponse_Gym;
import com.sideproject.withpt.application.lesson.repository.dto.QMemberLessonInfoResponse_Lesson;
import com.sideproject.withpt.application.lesson.repository.dto.QMemberLessonInfoResponse_Trainer;
import com.sideproject.withpt.application.lesson.repository.dto.QTrainerLessonInfoResponse;
import com.sideproject.withpt.application.lesson.repository.dto.QTrainerLessonInfoResponse_Gym;
import com.sideproject.withpt.application.lesson.repository.dto.QTrainerLessonInfoResponse_Lesson;
import com.sideproject.withpt.application.lesson.repository.dto.QTrainerLessonInfoResponse_Member;
import com.sideproject.withpt.application.lesson.repository.dto.TrainerLessonInfoResponse;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Member;
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
import org.springframework.util.CollectionUtils;
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
    public List<TrainerLessonInfoResponse> getTrainerLessonScheduleByDate(List<GymTrainer> gymTrainers, LocalDate date) {
        return jpaQueryFactory
            .select(
                new QTrainerLessonInfoResponse(
                    new QTrainerLessonInfoResponse_Lesson(
                        lesson.id,
                        lesson.schedule,
                        lesson.beforeSchedule,
                        lesson.status,
                        lesson.requester,
                        lesson.receiver,
                        lesson.registeredBy,
                        lesson.modifiedBy
                    ),
                    new QTrainerLessonInfoResponse_Member(
                        member.id,
                        member.name
                    ),
                    new QTrainerLessonInfoResponse_Gym(
                        gym.id,
                        gym.name
                    )
                )
            )
            .from(lesson)
            .join(lesson.member, member)
            .join(lesson.gymTrainer, gymTrainer)
            .join(gymTrainer.gym, gym)
            .where(
                gymTrainersIn(gymTrainers),
                lesson.schedule.date.eq(date),
                lesson.status.eq(LessonStatus.RESERVED)
                    .or(lesson.status.eq(LessonStatus.CANCELED))
                    .or(lesson.status.eq(LessonStatus.TIME_OUT_CANCELED))
            )
            .orderBy(
                lesson.schedule.time.asc(),
                gym.name.asc()
            )
            .fetch();
    }

    @Override
    public List<MemberLessonInfoResponse> getMemberLessonScheduleByDate(Member filteringMember, LocalDate date) {
        return jpaQueryFactory
            .select(
                new QMemberLessonInfoResponse(
                    new QMemberLessonInfoResponse_Lesson(
                        lesson.id,
                        lesson.schedule,
                        lesson.beforeSchedule,
                        lesson.status,
                        lesson.requester,
                        lesson.receiver,
                        lesson.registeredBy,
                        lesson.modifiedBy
                    ),
                    new QMemberLessonInfoResponse_Trainer(
                        trainer.id,
                        trainer.name
                    ),
                    new QMemberLessonInfoResponse_Gym(
                        gym.id,
                        gym.name
                    )
                )
            )
            .from(lesson)
            .join(lesson.member, member)
            .join(lesson.gymTrainer, gymTrainer)
            .join(gymTrainer.gym, gym)
            .join(gymTrainer.trainer, trainer)
            .where(
                member.eq(filteringMember),
                lesson.status.eq(LessonStatus.RESERVED),
                lessonScheduleDateEq(date)
            )
            .orderBy(
                lesson.schedule.date.asc(),
                lesson.schedule.time.asc()
            )
            .fetch();
    }

    @Override
    public List<LocalDate> getTrainerLessonScheduleOfMonth(List<GymTrainer> gymTrainers, YearMonth yearMonth) {

        DateTemplate<String> localDateTemplate = Expressions.dateTemplate(
            String.class,
            "CONCAT(YEAR({0}), '-', LPAD(MONTH({0}), 2, '0'))",
            lesson.schedule.date,
            ConstantImpl.create("%Y-%m")
        );

        return jpaQueryFactory
            .select(
                lesson.schedule.date
            )
            .from(lesson)
            .where(
                gymTrainersIn(gymTrainers),
                localDateTemplate.eq(yearMonth.toString()),
                // 승인 대기 중이 아닌 수업들 다 조회
                lesson.status.ne(LessonStatus.PENDING_APPROVAL)
            ).distinct()
            .orderBy(lesson.schedule.date.asc())
            .fetch();
    }

    @Override
    public TrainerLessonInfoResponse findLessonScheduleInfoBy(Long lessonId) {
        return jpaQueryFactory
            .select(
                new QTrainerLessonInfoResponse(
                    new QTrainerLessonInfoResponse_Lesson(
                        lesson.id,
                        lesson.schedule,
                        lesson.beforeSchedule,
                        lesson.status,
                        lesson.requester,
                        lesson.receiver,
                        lesson.registeredBy,
                        lesson.modifiedBy
                    ),
                    new QTrainerLessonInfoResponse_Member(
                        member.id,
                        member.name
                    ),
                    new QTrainerLessonInfoResponse_Gym(
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

    private BooleanExpression memberNameContains(String name) {
        return StringUtils.hasText(name) ? lesson.member.name.contains(name) : null;
    }

    private BooleanExpression trainerEq(Trainer trainer) {
        return ObjectUtils.isEmpty(trainer) ? null : lesson.trainer.eq(trainer);
    }

    private BooleanExpression gymEq(Gym gym) {
        return ObjectUtils.isEmpty(gym) ? null : lesson.gym.eq(gym);
    }

    private BooleanExpression gymTrainersIn(List<GymTrainer> gymTrainers) {
        return CollectionUtils.isEmpty(gymTrainers) ? null : lesson.gymTrainer.in(gymTrainers);
    }

    private BooleanExpression lessonScheduleDateEq(LocalDate date) {
        return ObjectUtils.isEmpty(date) ? null : lesson.schedule.date.eq(date);
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
