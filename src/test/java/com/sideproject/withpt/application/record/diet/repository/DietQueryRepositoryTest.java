package com.sideproject.withpt.application.record.diet.repository;

import static com.sideproject.withpt.application.type.MealCategory.BREAKFAST;
import static com.sideproject.withpt.application.type.MealCategory.DINNER;
import static com.sideproject.withpt.application.type.MealCategory.LUNCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.diet.repository.response.DietFoodDto;
import com.sideproject.withpt.application.record.diet.repository.response.DietInfoDto;
import com.sideproject.withpt.application.record.diet.repository.response.ImageDto;
import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Image;
import com.sideproject.withpt.domain.record.diet.DietFood;
import com.sideproject.withpt.domain.record.diet.DietInfo;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class DietQueryRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DietRepository dietRepository;

    @Autowired
    private DietInfoRepository dietInfoRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DietQueryRepository dietQueryRepository;

    @DisplayName("식단으로 하위에 있는 식단 정보, 식단 음식, 음식 이미지를 조회할 수 있다.")
    @Test
    void findAllDietInfoAndDietFoodByDiets() {
        // given
        DietType dietType = DietType.DIET;
        LocalDate uploadDate = LocalDate.of(2024, 9, 7);

        Member member = saveMember(dietType);
        Diets diets = saveDiets(dietType, member, uploadDate);

        DietFood dietFood1 = createDietFood("음식1");
        DietFood dietFood2 = createDietFood("음식2");
        DietFood dietFood3 = createDietFood("음식3");
        List<DietFood> dietFoods = List.of(dietFood1, dietFood2, dietFood3);

        DietInfo dietInfo1 = saveDietInfo(diets, BREAKFAST, LocalDateTime.of(2024, 9, 7, 8, 30), dietFoods);
        DietInfo dietInfo2 = saveDietInfo(diets, LUNCH, LocalDateTime.of(2024, 9, 7, 12, 0), dietFoods);
        DietInfo dietInfo3 = saveDietInfo(diets, DINNER, LocalDateTime.of(2024, 9, 7, 19, 20), dietFoods);

        String targetUsageIdentificationId = "DIET_" + diets.getId() + "/DIETINFO_" + dietInfo1.getId();

        saveImage(member, targetUsageIdentificationId, uploadDate);
        saveImage(member, targetUsageIdentificationId, uploadDate);
        saveImage(member, "DIET_" + diets.getId() + "/DIETINFO_" + dietInfo2.getId(), uploadDate);
        saveImage(member, "DIET_" + diets.getId() + "/DIETINFO_" + dietInfo3.getId(), uploadDate);

        // when
        List<DietInfoDto> dietInfoDtos = dietQueryRepository.findAllDietInfoAndDietFoodByDiets(member, diets);

        // then
        assertThat(dietInfoDtos).hasSize(3)
            .extracting("mealTime", "mealCategory")
            .containsExactly(
                tuple(LocalDateTime.of(2024, 9, 7, 8, 30), BREAKFAST),
                tuple(LocalDateTime.of(2024, 9, 7, 12, 0), LUNCH),
                tuple(LocalDateTime.of(2024, 9, 7, 19, 20), DINNER)
            );

        List<DietFoodDto> dietFoodDtos = dietInfoDtos.get(0).getDietFoods();
        assertThat(dietFoodDtos).hasSize(3)
            .extracting("name")
            .containsExactly(
                "음식1", "음식2", "음식3"
            );

        List<ImageDto> imageDtos = dietInfoDtos.get(0).getImages();
        assertThat(imageDtos).hasSize(2)
            .extracting("usageIdentificationId")
            .contains(targetUsageIdentificationId);
    }

    private Member saveMember(final DietType dietType) {
        return memberRepository.save(
            Member.builder()
                .email("test@test.com")
                .name("member1")
                .dietType(dietType)
                .role(Role.MEMBER)
                .build()
        );
    }

    private Diets saveDiets(DietType dietType, Member member, LocalDate uploadDate) {
        return dietRepository.save(
            Diets.builder()
                .member(member)
                .uploadDate(uploadDate)
                .totalCalorie(1200)
                .totalProtein(200)
                .totalCarbohydrate(300)
                .totalFat(400)
                .targetDietType(dietType)
                .build()
        );
    }

    private DietInfo saveDietInfo(Diets diets, MealCategory mealCategory, LocalDateTime mealTime, List<DietFood> dietFoods) {
        return dietInfoRepository.save(
            DietInfo.builder()
                .diets(diets)
                .mealCategory(mealCategory)
                .mealTime(mealTime)
                .totalCalorie(1000)
                .totalProtein(300)
                .totalCarbohydrate(200)
                .totalFat(100)
                .dietFoods(dietFoods)
                .build()
        );
    }

    private DietFood createDietFood(String name) {
        return DietFood.builder()
            .name(name)
            .capacity(100)
            .units("g")
            .calories(1200)
            .carbohydrate(150)
            .protein(200)
            .fat(100)
            .build();
    }

    private void saveImage(Member member, String usageIdentificationId, LocalDate uploadDate) {
        imageRepository.save(Image.builder()
            .member(member)
            .usageIdentificationId(usageIdentificationId)
            .usages(Usages.MEAL)
            .uploadDate(uploadDate)
            .url("URL")
            .uploadUrlPath("uploadUrlPath")
            .attachType("image/png")
            .build());
    }

}