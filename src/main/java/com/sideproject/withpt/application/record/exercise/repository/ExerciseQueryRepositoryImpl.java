package com.sideproject.withpt.application.record.exercise.repository;

import static com.sideproject.withpt.domain.record.exercise.QExercise.exercise;
import static com.sideproject.withpt.domain.record.exercise.QExerciseInfo.exerciseInfo;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ExerciseQueryRepositoryImpl implements ExerciseQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final ExerciseInfoRepository exerciseInfoRepository;

    @Override
    public Map<LocalDate, Exercise> findExercisesByYearMonth(Member member, int year, int month) {
        String searchYearMonth = YearMonth.of(year, month).toString();
        StringTemplate exerciseUploadDate = Expressions.stringTemplate(
            "DATE_FORMAT({0}, '%Y-%m')",
            exercise.uploadDate
        );

        return jpaQueryFactory.selectFrom(exercise)
            .where(exercise.member.eq(member).and(exerciseUploadDate.eq(searchYearMonth)))
            .orderBy(exercise.uploadDate.asc())
            .fetch()
            .stream().collect(Collectors.toMap(
                Exercise::getUploadDate,
                exercise -> exercise
            ));
    }

    @Override
    public Optional<ExerciseInfo> findExerciseInfoById(Long id) {
        try {
            return Optional.ofNullable(
                jpaQueryFactory.selectFrom(exerciseInfo)
                    .where(exerciseInfo.id.eq(id))
                    .fetchOne()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteExerciseInfoById(Long id) {
        exerciseInfoRepository.deleteById(id);
    }
}
