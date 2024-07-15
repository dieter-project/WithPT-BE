package com.sideproject.withpt.application.diet.repository;

import static com.sideproject.withpt.domain.record.diet.QDiets.diets;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DietQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<Diets> findByMemberAndUploadDate(Member member, LocalDate uploadDate) {

        DateTemplate<LocalDate> selectedDateTemplate = Expressions.dateTemplate(
            LocalDate.class,
            "DATE_FORMAT({0}, {1})",
            uploadDate,
            ConstantImpl.create("%Y-%m-%d")
        );

        DateTemplate<LocalDate> createdDateTemplate = Expressions.dateTemplate(
            LocalDate.class,
            "DATE_FORMAT({0}, {1})",
            diets.uploadDate,
            Expressions.constant("%Y-%m-%d")
        );

        return Optional.ofNullable(
            jpaQueryFactory
                .selectFrom(diets)
                .where(
                    diets.member.eq(member)
                        .and(createdDateTemplate.eq(selectedDateTemplate))
                )
                .fetchOne()
        );
    }

}
