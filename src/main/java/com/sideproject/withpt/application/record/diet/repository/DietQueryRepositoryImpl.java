package com.sideproject.withpt.application.record.diet.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.sideproject.withpt.domain.record.QImage.image;
import static com.sideproject.withpt.domain.record.diet.QDietFood.dietFood;
import static com.sideproject.withpt.domain.record.diet.QDietInfo.dietInfo;
import static com.sideproject.withpt.domain.record.diet.QDiets.diets;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.record.diet.repository.response.DietInfoDto;
import com.sideproject.withpt.application.record.diet.repository.response.QDietFoodDto;
import com.sideproject.withpt.application.record.diet.repository.response.QDietInfoDto;
import com.sideproject.withpt.application.record.diet.repository.response.QImageDto;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DietQueryRepositoryImpl implements DietQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Diets> findAllPageableByMemberAndUploadDate(Member member, LocalDate uploadDate, Pageable pageable) {
        List<Diets> contents = jpaQueryFactory
            .selectFrom(diets)
            .where(
                diets.member.eq(member),
                diets.uploadDate.loe(uploadDate)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(diets.uploadDate.desc())
            .fetch();

        boolean hasNext = false;

        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    @Override
    public List<DietInfoDto> findAllDietInfoAndDietFoodByDiets(Member member, Diets diets) {

        try {
            List<DietInfoDto> dietInfoDtos = jpaQueryFactory
                .selectFrom(dietInfo)
                .leftJoin(dietFood).on(dietFood.dietInfo.eq(dietInfo))
                .where(dietInfo.diets.eq(diets))
                .orderBy(dietInfo.dietTime.asc(), dietFood.id.asc())
                .transform(groupBy(dietInfo.id)
                    .list(new QDietInfoDto(
                        dietInfo.id,
                        dietInfo.dietCategory,
                        dietInfo.dietTime,
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
                                image.usageType,
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

    @Override
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

    private BooleanExpression uploadDateEq(LocalDate uploadDate) {
        return ObjectUtils.isEmpty(uploadDate) ? null : diets.uploadDate.eq(uploadDate);
    }
}
