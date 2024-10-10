package com.sideproject.withpt.application.record.diet.repository;

import static com.sideproject.withpt.common.type.DietCategory.BREAKFAST;
import static com.sideproject.withpt.common.type.DietCategory.DINNER;
import static com.sideproject.withpt.common.type.DietCategory.LUNCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.diet.repository.response.DietFoodDto;
import com.sideproject.withpt.application.record.diet.repository.response.DietInfoDto;
import com.sideproject.withpt.application.record.diet.repository.response.ImageDto;
import com.sideproject.withpt.common.type.DietCategory;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Usages;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Image;
import com.sideproject.withpt.domain.record.diet.DietFood;
import com.sideproject.withpt.domain.record.diet.DietInfo;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
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
    private DietQueryRepositoryImpl dietQueryRepository;

    @DisplayName("uploadDate 기준으로 n 건의 식단 데이터 조회")
    @Test
    void findAllPageableByMemberAndUploadDate() {
        //given
        Member member = saveMember(DietType.Carb_Protein_Fat);

        LocalDate uploadDate1 = LocalDate.of(2024, 10, 3);
        dietRepository.save(createDiets(DietType.Carb_Protein_Fat, member, uploadDate1));

        LocalDate uploadDate2 = LocalDate.of(2024, 10, 5);
        dietRepository.save(createDiets(DietType.DIET, member, uploadDate2));

        LocalDate uploadDate3 = LocalDate.of(2024, 10, 8);
        dietRepository.save(createDiets(DietType.KETO, member, uploadDate3));

        LocalDate uploadDate4 = LocalDate.of(2024, 10, 10);
        dietRepository.save(createDiets(DietType.PROTEIN, member, uploadDate4));

        LocalDate requestDate = LocalDate.of(2024, 10, 12);
        Pageable pageable = PageRequest.of(0, 3);

        //when
        Slice<Diets> result = dietRepository.findAllPageableByMemberAndUploadDate(member, requestDate, pageable);

        //then
       assertThat(result.getSize()).isEqualTo(3);
       assertThat(result.getContent()).hasSize(3)
           .extracting("targetDietType", "uploadDate")
           .containsExactly(
               tuple(DietType.PROTEIN, uploadDate4),
               tuple(DietType.KETO, uploadDate3),
               tuple(DietType.DIET, uploadDate2)
           );
    }

    @DisplayName("식단으로 하위에 있는 식단 정보, 식단 음식, 음식 이미지를 조회할 수 있다.")
    @Test
    void findAllDietInfoAndDietFoodByDiets() {
        // given
        DietType dietType = DietType.DIET;
        LocalDate uploadDate = LocalDate.of(2024, 9, 7);

        Member member = saveMember(dietType);
        Diets diets = createDiets(dietType, member, uploadDate);
        dietRepository.save(diets);

        DietFood dietFood1 = createDietFood("음식1");
        DietFood dietFood2 = createDietFood("음식2");
        DietFood dietFood3 = createDietFood("음식3");
        DietFood dietFood4 = createDietFood("음식4");
        DietFood dietFood5 = createDietFood("음식5");
        DietFood dietFood6 = createDietFood("음식6");
        List<DietFood> dietFoods1 = List.of(dietFood1, dietFood2);
        List<DietFood> dietFoods2 = List.of(dietFood3, dietFood4);
        List<DietFood> dietFoods3 = List.of(dietFood5, dietFood6);

        DietInfo dietInfo1 = createDietInfo(diets, BREAKFAST, LocalDateTime.of(2024, 9, 7, 8, 30), dietFoods1);
        DietInfo dietInfo2 = createDietInfo(diets, LUNCH, LocalDateTime.of(2024, 9, 7, 12, 0), dietFoods2);
        DietInfo dietInfo3 = createDietInfo(diets, DINNER, LocalDateTime.of(2024, 9, 7, 19, 20), dietFoods3);
        dietInfoRepository.saveAll(List.of(dietInfo1, dietInfo2, dietInfo3));

        String targetUsageIdentificationId = "DIET_" + diets.getId() + "/DIETINFO_" + dietInfo1.getId();

        saveImage(member, targetUsageIdentificationId, uploadDate);
        saveImage(member, targetUsageIdentificationId, uploadDate);
        saveImage(member, "DIET_" + diets.getId() + "/DIETINFO_" + dietInfo2.getId(), uploadDate);
        saveImage(member, "DIET_" + diets.getId() + "/DIETINFO_" + dietInfo3.getId(), uploadDate);

        // when
        List<DietInfoDto> dietInfoDtos = dietQueryRepository.findAllDietInfoAndDietFoodByDiets(member, diets);

        // then
        assertThat(dietInfoDtos).hasSize(3)
            .extracting("dietTime", "dietCategory")
            .containsExactly(
                tuple(LocalDateTime.of(2024, 9, 7, 8, 30), BREAKFAST),
                tuple(LocalDateTime.of(2024, 9, 7, 12, 0), LUNCH),
                tuple(LocalDateTime.of(2024, 9, 7, 19, 20), DINNER)
            );

        List<DietFoodDto> dietFoodDtos = dietInfoDtos.get(0).getDietFoods();
        assertThat(dietFoodDtos).hasSize(2)
            .extracting("name")
            .containsExactly(
                "음식1", "음식2"
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

    private Diets createDiets(DietType dietType, Member member, LocalDate uploadDate) {
        return Diets.builder()
            .member(member)
            .uploadDate(uploadDate)
            .totalCalorie(1200)
            .totalProtein(200)
            .totalCarbohydrate(300)
            .totalFat(400)
            .targetDietType(dietType)
            .build();
    }

    private DietInfo createDietInfo(Diets diets, DietCategory dietCategory, LocalDateTime dietTime, List<DietFood> dietFoods) {
        return DietInfo.builder()
            .diets(diets)
            .dietCategory(dietCategory)
            .dietTime(dietTime)
            .totalCalorie(1000)
            .totalProtein(300)
            .totalCarbohydrate(200)
            .totalFat(100)
            .dietFoods(dietFoods)
            .build();
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
            .usages(Usages.DIET)
            .uploadDate(uploadDate)
            .url("URL")
            .uploadUrlPath("uploadUrlPath")
            .attachType("image/png")
            .build());
    }

}