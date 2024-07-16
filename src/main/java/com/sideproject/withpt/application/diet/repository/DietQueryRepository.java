package com.sideproject.withpt.application.diet.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.sideproject.withpt.domain.record.QImage.image;
import static com.sideproject.withpt.domain.record.diet.QDietFood.dietFood;
import static com.sideproject.withpt.domain.record.diet.QDietInfo.dietInfo;
import static com.sideproject.withpt.domain.record.diet.QDiets.diets;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.diet.controller.response.DailyDietResponse;
import com.sideproject.withpt.application.diet.controller.response.DietInfoResponse;
import com.sideproject.withpt.application.diet.controller.response.QDailyDietResponse;
import com.sideproject.withpt.application.diet.controller.response.QDietInfoResponse;
import com.sideproject.withpt.application.diet.controller.response.QDietInfoResponse_DietFoodResponse;
import com.sideproject.withpt.application.diet.controller.response.QDietInfoResponse_ImageResponse;
import com.sideproject.withpt.application.diet.exception.DietErrorCode;
import com.sideproject.withpt.application.diet.exception.DietException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    public DailyDietResponse findDietByUploadDate(Member member, LocalDate uploadDate) {

        DateTemplate<LocalDate> uploadDateTemplate = Expressions.dateTemplate(
            LocalDate.class,
            "DATE_FORMAT({0}, {1})",
            uploadDate,
            ConstantImpl.create("%Y-%m-%d")
        );

        DateTemplate<LocalDate> dietUploadDateTemplate = Expressions.dateTemplate(
            LocalDate.class,
            "DATE_FORMAT({0}, {1})",
            diets.uploadDate,
            Expressions.constant("%Y-%m-%d")
        );

        try {
            DailyDietResponse dailyDietResponse = jpaQueryFactory
                .select(
                    new QDailyDietResponse(
                        diets.id,
                        diets.uploadDate,
                        diets.feedback,
                        diets.totalCalorie,
                        diets.totalProtein,
                        diets.totalCarbohydrate,
                        diets.totalFat,
                        diets.targetDietType
                    )
                )
                .from(diets)
                .where(diets.member.eq(member).and(uploadDateTemplate.eq(dietUploadDateTemplate)))
                .fetchOne();

            List<DietInfoResponse> dietInfoResponses = jpaQueryFactory
                .selectFrom(dietInfo)
                .leftJoin(dietFood).on(dietFood.dietInfo.eq(dietInfo))
                .where(dietInfo.diets.id.eq(dailyDietResponse.getId()))
                .orderBy(dietInfo.mealTime.asc(), dietFood.id.asc())
                .transform(groupBy(dietInfo.id)
                    .list(new QDietInfoResponse(
                        dietInfo.id,
                        dietInfo.mealCategory,
                        dietInfo.mealTime,
                        dietInfo.totalCalorie,
                        dietInfo.totalProtein,
                        dietInfo.totalCarbohydrate,
                        dietInfo.totalFat,
                        list(new QDietInfoResponse_DietFoodResponse(
                            dietFood.id,
                            dietFood.name,
                            dietFood.capacity,
                            dietFood.units,
                            dietFood.calories,
                            dietFood.carbohydrate,
                            dietFood.protein,
                            dietFood.fat
                        )),
                        Expressions.stringTemplate(
                            "CONCAT('DIET_', {0}, '/DIETINFO_', {1})",
                            dietInfo.diets.id.stringValue(),
                            dietInfo.id.stringValue()
                        )
                    ))
                );

            for (DietInfoResponse dietInfoResponse : dietInfoResponses) {
                dietInfoResponse.setImages(
                    jpaQueryFactory
                        .select(
                            new QDietInfoResponse_ImageResponse(
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
                            image.usageIdentificationId.eq(
                                dietInfoResponse.getImageUsageIdentificationId()
                            )
                        )).fetch()
                );
            }

            dailyDietResponse.setDietInfos(dietInfoResponses);
            return dailyDietResponse;

        } catch (Exception e) {
            throw new DietException(DietErrorCode.DIET_NOT_EXIST);
        }
    }

    public DietInfoResponse findDietInfoById(Member member, Long dietInfoId) {

        try {
            List<DietInfoResponse> dietInfoResponses = jpaQueryFactory
                .select(dietInfo)
                .from(dietInfo)
                .leftJoin(dietFood).on(dietFood.dietInfo.eq(dietInfo))
                .where(dietInfo.id.eq(dietInfoId))
                .orderBy(dietInfo.mealTime.asc(), dietFood.id.asc())
                .transform(groupBy(dietInfo.id)
                    .list(new QDietInfoResponse(
                        dietInfo.id,
                        dietInfo.mealCategory,
                        dietInfo.mealTime,
                        dietInfo.totalCalorie,
                        dietInfo.totalProtein,
                        dietInfo.totalCarbohydrate,
                        dietInfo.totalFat,
                        list(new QDietInfoResponse_DietFoodResponse(
                            dietFood.id,
                            dietFood.name,
                            dietFood.capacity,
                            dietFood.units,
                            dietFood.calories,
                            dietFood.carbohydrate,
                            dietFood.protein,
                            dietFood.fat
                        )),
                        Expressions.stringTemplate(
                            "CONCAT('DIET_', {0}, '/DIETINFO_', {1})",
                            dietInfo.diets.id.stringValue(),
                            dietInfo.id.stringValue()
                        )
                    ))
                );

            DietInfoResponse dietInfoResponse = dietInfoResponses.get(0);
            dietInfoResponse.setImages(
                jpaQueryFactory
                    .select(
                        new QDietInfoResponse_ImageResponse(
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
                        image.usageIdentificationId.eq(
                            dietInfoResponse.getImageUsageIdentificationId()
                        )
                    )).fetch()
            );

            return dietInfoResponse;
        } catch (Exception e) {
            throw new DietException(DietErrorCode.DIET_NOT_EXIST);
        }
    }
}
