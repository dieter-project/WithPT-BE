package com.sideproject.withpt.application.record.diet.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.sideproject.withpt.domain.record.QImage.image;
import static com.sideproject.withpt.domain.record.diet.QDietFood.dietFood;
import static com.sideproject.withpt.domain.record.diet.QDietInfo.dietInfo;
import static com.sideproject.withpt.domain.record.diet.QDiets.diets;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.record.diet.exception.DietErrorCode;
import com.sideproject.withpt.application.record.diet.exception.DietException;
import com.sideproject.withpt.application.record.diet.repository.response.DietInfoDto;
import com.sideproject.withpt.application.record.diet.repository.response.QDietFoodDto;
import com.sideproject.withpt.application.record.diet.repository.response.QDietInfoDto;
import com.sideproject.withpt.application.record.diet.repository.response.QImageDto;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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

    public List<DietInfoDto> findAllDietInfoAndDietFoodByDiets(Member member, Diets diets) {

        try {
            List<DietInfoDto> dietInfoDtos = jpaQueryFactory
                .selectFrom(dietInfo)
                .leftJoin(dietFood).on(dietFood.dietInfo.eq(dietInfo))
                .where(dietInfo.diets.eq(diets))
                .orderBy(dietInfo.mealTime.asc(), dietFood.id.asc())
                .transform(groupBy(dietInfo.id)
                    .list(new QDietInfoDto(
                        dietInfo.id,
                        dietInfo.mealCategory,
                        dietInfo.mealTime,
                        dietInfo.totalCalorie,
                        dietInfo.totalProtein,
                        dietInfo.totalCarbohydrate,
                        dietInfo.totalFat,
                        list(new QDietFoodDto(
                            dietFood.id,
                            dietFood.name,
                            dietFood.capacity,
                            dietFood.units,
                            dietFood.calories,
                            dietFood.carbohydrate,
                            dietFood.protein,
                            dietFood.fat
                        ))
                    ))
                );

            for (DietInfoDto dietInfoDto : dietInfoDtos) {

                String imageUsageIdentificationId = "DIET_" + diets.getId() + "/DIETINFO_" + dietInfoDto.getId();

                dietInfoDto.setImages(
                    jpaQueryFactory
                        .select(
                            new QImageDto(
                                image.id,
                                image.usageIdentificationId,
                                image.usages,
                                image.uploadDate,
                                image.url,
                                image.attachType
                            )
                        )
                        .from(image)
                        .where(image.member.eq(member).and(
                            image.usageIdentificationId.eq(imageUsageIdentificationId)
                        )).fetch()
                );
            }

            return dietInfoDtos;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DietInfoDto findDietInfoById(Member member, Long dietInfoId) {

        try {
            StringTemplate imageUsageIdentificationId = Expressions.stringTemplate(
                "CONCAT('DIET_', {0}, '/DIETINFO_', {1})",
                dietInfo.diets.id.stringValue(),
                dietInfo.id.stringValue()
            );

            List<DietInfoDto> dietInfoDtos = jpaQueryFactory
                .select(dietInfo)
                .from(dietInfo)
                .leftJoin(dietFood).on(dietFood.dietInfo.eq(dietInfo))
                .where(dietInfo.id.eq(dietInfoId))
                .orderBy(dietInfo.mealTime.asc(), dietFood.id.asc())
                .transform(groupBy(dietInfo.id)
                    .list(new QDietInfoDto(
                        dietInfo.id,
                        dietInfo.mealCategory,
                        dietInfo.mealTime,
                        dietInfo.totalCalorie,
                        dietInfo.totalProtein,
                        dietInfo.totalCarbohydrate,
                        dietInfo.totalFat,
                        list(new QDietFoodDto(
                            dietFood.id,
                            dietFood.name,
                            dietFood.capacity,
                            dietFood.units,
                            dietFood.calories,
                            dietFood.carbohydrate,
                            dietFood.protein,
                            dietFood.fat
                        ))
                    ))
                );

            DietInfoDto dietInfoDto = dietInfoDtos.get(0);
            dietInfoDto.setImages(
                jpaQueryFactory
                    .select(
                        new QImageDto(
                            image.id,
                            image.usageIdentificationId,
                            image.usages,
                            image.uploadDate,
                            image.url,
                            image.attachType
                        )
                    )
                    .from(image)
                    .where(image.member.eq(member).and(
                        image.usageIdentificationId.eq(imageUsageIdentificationId)
                    )).fetch()
            );

            return dietInfoDto;
        } catch (Exception e) {
            throw new DietException(DietErrorCode.DIET_NOT_EXIST);
        }
    }

    public Map<LocalDate, Diets> findDietsByYearMonth(Member member, int year, int month) {
        // Map<String, Diets>
        String searchYearMonth = YearMonth.of(year, month).toString();
        StringTemplate dietsUploadDate = Expressions.stringTemplate(
            "DATE_FORMAT({0}, '%Y-%m')",
            diets.uploadDate
        );

        return jpaQueryFactory.selectFrom(diets)
            .where(diets.member.eq(member).and(dietsUploadDate.eq(searchYearMonth)))
            .orderBy(diets.uploadDate.desc())
            .fetch()
            .stream()
            .collect(
                Collectors.toMap(Diets::getUploadDate,
                    diets -> diets)
            );

    }
}
