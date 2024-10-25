package com.sideproject.withpt.application.lesson.repository;

import static com.sideproject.withpt.domain.lesson.QLesson.lesson;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.common.type.LessonStatus;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.user.member.Member;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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
    public List<Lesson> getTrainerLessonScheduleByDate(List<GymTrainer> gymTrainers, LocalDate date) {
        return jpaQueryFactory
            .selectFrom(lesson)
            .where(
                gymTrainersIn(gymTrainers),
                lesson.schedule.date.eq(date),
                lesson.status.eq(LessonStatus.RESERVED)
                    .or(lesson.status.eq(LessonStatus.CANCELED))
                    .or(lesson.status.eq(LessonStatus.TIME_OUT_CANCELED))
            )
            .orderBy(
                lesson.schedule.time.asc()
            )
            .fetch();
    }

    @Override
    public List<Lesson> getMemberLessonScheduleByDate(Member filteringMember, LocalDate date) {
        return jpaQueryFactory
            .selectFrom(lesson)
            .where(
                lesson.member.eq(filteringMember),
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
    public List<LocalDate> getMemberLessonScheduleOfMonth(List<GymTrainer> gymTrainers, Member member, YearMonth yearMonth) {

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
                lesson.member.eq(member),
                localDateTemplate.eq(yearMonth.toString()),
                lesson.status.in(List.of(LessonStatus.COMPLETION, LessonStatus.RESERVED))
            ).distinct()
            .orderBy(lesson.schedule.date.asc())
            .fetch();
    }

    @Override
    public Slice<Lesson> findAllRegisteredByAndLessonStatus(Role role, LessonStatus status, List<GymTrainer> gymTrainers, Pageable pageable) {
        List<Lesson> contents = jpaQueryFactory
            .selectFrom(lesson)
            .where(
                gymTrainersIn(gymTrainers),
                lesson.status.eq(status),
                lesson.registeredBy.eq(role),
                lesson.modifiedBy.isNull()
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(
                lesson.schedule.date.asc(),
                lesson.schedule.time.asc()
            )
            .fetch();

        boolean hasNext = false;

        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    @Override
    public Slice<Lesson> findAllModifiedByAndLessonStatus(Role role, LessonStatus status, List<GymTrainer> gymTrainers, Pageable pageable) {
        List<Lesson> contents = jpaQueryFactory
            .selectFrom(lesson)
            .where(
                gymTrainersIn(gymTrainers),
                lesson.status.eq(status),
                lesson.modifiedBy.eq(role)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(
                lesson.schedule.date.asc(),
                lesson.schedule.time.asc()
            )
            .fetch();

        boolean hasNext = false;

        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    private BooleanExpression gymTrainersIn(List<GymTrainer> gymTrainers) {
        return CollectionUtils.isEmpty(gymTrainers) ? null : lesson.gymTrainer.in(gymTrainers);
    }

    private BooleanExpression lessonScheduleDateEq(LocalDate date) {
        return ObjectUtils.isEmpty(date) ? null : lesson.schedule.date.eq(date);
    }
}
