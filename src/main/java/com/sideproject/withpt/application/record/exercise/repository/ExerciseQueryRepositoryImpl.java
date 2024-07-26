package com.sideproject.withpt.application.record.exercise.repository;

import static com.sideproject.withpt.domain.record.exercise.QExercise.exercise;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ExerciseQueryRepositoryImpl implements ExerciseQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

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

    //    @Override
//    public List<Exercise> findExercisesByYearMonth(Member member, int year, int month) {
//        String searchYearMonth = YearMonth.of(year, month).toString();
//        StringTemplate exerciseUploadDate = Expressions.stringTemplate(
//            "DATE_FORMAT({0}, '%Y-%m')",
//            exercise.uploadDate
//        );
//
//        return jpaQueryFactory.selectFrom(exercise)
//            .where(exercise.member.eq(member).and(exerciseUploadDate.eq(searchYearMonth)))
//            .orderBy(exercise.uploadDate.asc())
//            .fetch();
//    }
}
