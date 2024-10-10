package com.sideproject.withpt.application.record.diet.service;

import static com.sideproject.withpt.common.type.DietCategory.BREAKFAST;
import static com.sideproject.withpt.common.type.DietCategory.DINNER;
import static com.sideproject.withpt.common.type.DietCategory.LUNCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.diet.controller.request.DietFoodRequest;
import com.sideproject.withpt.application.record.diet.controller.request.EditDietInfoRequest;
import com.sideproject.withpt.application.record.diet.controller.request.SaveDietRequest;
import com.sideproject.withpt.application.record.diet.repository.DietFoodRepository;
import com.sideproject.withpt.application.record.diet.repository.DietInfoRepository;
import com.sideproject.withpt.application.record.diet.repository.DietRepository;
import com.sideproject.withpt.application.record.diet.service.response.DailyDietResponse;
import com.sideproject.withpt.application.record.diet.service.response.DietFoodResponse;
import com.sideproject.withpt.application.record.diet.service.response.DietInfoResponse;
import com.sideproject.withpt.application.record.diet.service.response.ImageResponse;
import com.sideproject.withpt.common.type.DietCategory;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Usages;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Image;
import com.sideproject.withpt.domain.record.diet.DietFood;
import com.sideproject.withpt.domain.record.diet.DietInfo;
import com.sideproject.withpt.domain.record.diet.Diets;
import com.sideproject.withpt.domain.record.diet.utils.DietNutritionalStatistics;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class DietServiceTest {

    @Autowired
    private DietService dietService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DietRepository dietRepository;

    @Autowired
    private DietInfoRepository dietInfoRepository;

    @Autowired
    private DietFoodRepository dietFoodRepository;

    @Autowired
    private ImageRepository imageRepository;

//    @AfterEach
//    void tearDown() {
//        dietFoodRepository.deleteAllInBatch();
//        dietInfoRepository.deleteAllInBatch();
//        dietRepository.deleteAllInBatch();
//        imageRepository.deleteAllInBatch();
//        memberRepository.deleteAllInBatch();
//    }

    @DisplayName("DB에 식단 데이터가 존재하지 않을 떄 식단 정보가 신규 생성된다.")
    @Test
    void saveOrUpdateDietWhenDateDoesNotExistInDB() {
        // given
        LocalDate uploadDate = LocalDate.of(2024, 9, 22);
        Member member = saveMember();

        DietFoodRequest dietFoodRequest1 = createaDietFoodRequest("sample1", 100, 1, 2, 3);
        DietFoodRequest dietFoodRequest2 = createaDietFoodRequest("sample2", 200, 1, 2, 3);
        SaveDietRequest request = SaveDietRequest.builder()
            .uploadDate(uploadDate)
            .dietCategory(DINNER)
            .dietTime(LocalTime.of(8, 45))
            .dietFoods(List.of(dietFoodRequest1, dietFoodRequest2))
            .build();

        // when
        dietService.saveOrUpdateDiet(member.getId(), request, null);

        // then
        Diets diets = dietRepository.findByMemberAndUploadDate(member, uploadDate).get();
        assertThat(diets)
            .extracting("uploadDate", "totalCalorie", "totalCarbohydrate", "totalProtein", "totalFat")
            .contains(uploadDate, 300.0, 2.0, 4.0, 6.0);

        List<DietInfo> dietInfos = dietInfoRepository.findAll();
        assertThat(dietInfos).hasSize(1)
            .extracting("dietCategory", "dietTime", "totalCalorie", "totalCarbohydrate", "totalProtein", "totalFat")
            .contains(
                tuple(DINNER, LocalDateTime.of(uploadDate, LocalTime.of(8, 45)), 300.0, 2.0, 4.0, 6.0)
            );

        List<DietFood> dietFoods = dietFoodRepository.findAll();
        assertThat(dietFoods).hasSize(2)
            .extracting("name", "calories", "carbohydrate", "protein", "fat")
            .containsExactlyInAnyOrder(
                tuple("sample1", 100.0, 1.0, 2.0, 3.0),
                tuple("sample2", 200.0, 1.0, 2.0, 3.0)
            );
    }

    @DisplayName("DB에 식단 데이터가 이미 존재할 때 식단 정보가 추가되면서 업데이트 된다.")
    @Test
    void saveOrUpdateDietWhenDateAlreadyExistInDB() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 22);

        DietFood dietFood1 = createDietFood("음식1", 1, 150, 200, 100);
        DietFood dietFood2 = createDietFood("음식2", 2, 150, 200, 100);
        DietFood dietFood3 = createDietFood("음식3", 3, 150, 200, 100);
        DietFood dietFood4 = createDietFood("음식4", 4, 150, 200, 100);
        List<DietFood> dietFoods1 = List.of(dietFood1, dietFood2);
        List<DietFood> dietFoods2 = List.of(dietFood3, dietFood4);

        Diets savedDiets = saveDiets(member, uploadDate, List.of(dietFoods1, dietFoods2));
        saveDietInfo(savedDiets, BREAKFAST, LocalDateTime.of(uploadDate, LocalTime.of(8, 30)), dietFoods1);
        saveDietInfo(savedDiets, LUNCH, LocalDateTime.of(uploadDate, LocalTime.of(12, 0)), dietFoods2);

        DietFoodRequest dietFoodRequest1 = createaDietFoodRequest("sample1", 100, 10, 10, 10);
        DietFoodRequest dietFoodRequest2 = createaDietFoodRequest("sample2", 200, 20, 20, 20);
        SaveDietRequest request = SaveDietRequest.builder()
            .uploadDate(uploadDate)
            .dietCategory(DINNER)
            .dietTime(LocalTime.of(8, 45))
            .dietFoods(List.of(dietFoodRequest1, dietFoodRequest2))
            .build();

        // when
        dietService.saveOrUpdateDiet(member.getId(), request, null);

        // then
        Diets diets = dietRepository.findByMemberAndUploadDate(member, uploadDate).get();
        assertThat(diets)
            .extracting("uploadDate", "totalCalorie", "totalCarbohydrate", "totalProtein", "totalFat")
            .contains(uploadDate, 310.0, 630.0, 830.0, 430.0);

        List<DietInfo> dietInfos = dietInfoRepository.findAll();
        assertThat(dietInfos).hasSize(3)
            .extracting("dietCategory", "dietTime", "totalCalorie", "totalCarbohydrate", "totalProtein", "totalFat")
            .containsExactlyInAnyOrder(
                tuple(BREAKFAST, LocalDateTime.of(uploadDate, LocalTime.of(8, 30)), 3.0, 300.0, 400.0, 200.0),
                tuple(LUNCH, LocalDateTime.of(uploadDate, LocalTime.of(12, 0)), 7.0, 300.0, 400.0, 200.0),
                tuple(DINNER, LocalDateTime.of(uploadDate, LocalTime.of(8, 45)), 300.0, 30.0, 30.0, 30.0)
            );

        List<DietFood> dietFoods = dietFoodRepository.findAll();
        assertThat(dietFoods).hasSize(6)
            .extracting("name", "calories", "carbohydrate", "protein", "fat")
            .containsExactlyInAnyOrder(
                tuple("음식1", 1.0, 150.0, 200.0, 100.0),
                tuple("음식2", 2.0, 150.0, 200.0, 100.0),
                tuple("음식3", 3.0, 150.0, 200.0, 100.0),
                tuple("음식4", 4.0, 150.0, 200.0, 100.0),
                tuple("sample1", 100.0, 10.0, 10.0, 10.0),
                tuple("sample2", 200.0, 20.0, 20.0, 20.0)
            );
    }

    @Nested
    @DisplayName("식단 조회")
    class FindDietByMemberAndUploadDate {

        @DisplayName("요청 날짜에 저장된 식단 정보를 조회할 수 있다.")
        @Test
        void findDietByMemberAndUploadDate() {
            // given
            final LocalDate uploadDate = LocalDate.of(2024, 9, 7);

            Member member = saveMember();

            DietFood dietFood1 = createDietFood("음식1", 1200, 150, 200, 100);
            DietFood dietFood2 = createDietFood("음식2", 1200, 150, 200, 100);
            DietFood dietFood3 = createDietFood("음식3", 1200, 150, 200, 100);
            DietFood dietFood4 = createDietFood("음식4", 1200, 150, 200, 100);
            DietFood dietFood5 = createDietFood("음식5", 1200, 150, 200, 100);
            DietFood dietFood6 = createDietFood("음식6", 1200, 150, 200, 100);
            List<DietFood> dietFoods1 = List.of(dietFood1, dietFood2);
            List<DietFood> dietFoods2 = List.of(dietFood3, dietFood4);
            List<DietFood> dietFoods3 = List.of(dietFood5, dietFood6);

            Diets diets = saveDiets(member, uploadDate, List.of(dietFoods1, dietFoods2, dietFoods3));
            DietInfo dietInfo1 = saveDietInfo(diets, BREAKFAST, LocalDateTime.of(2024, 9, 7, 8, 30), dietFoods1);
            DietInfo dietInfo2 = saveDietInfo(diets, LUNCH, LocalDateTime.of(2024, 9, 7, 12, 0), dietFoods2);
            DietInfo dietInfo3 = saveDietInfo(diets, DINNER, LocalDateTime.of(2024, 9, 7, 19, 20), dietFoods3);

            saveImage(member, "DIET_" + diets.getId() + "/DIETINFO_" + dietInfo1.getId(), uploadDate);
            saveImage(member, "DIET_" + diets.getId() + "/DIETINFO_" + dietInfo2.getId(), uploadDate);
            saveImage(member, "DIET_" + diets.getId() + "/DIETINFO_" + dietInfo3.getId(), uploadDate);


            // when
            DailyDietResponse dailyDietResponse = dietService.findDietByMemberAndUploadDate(uploadDate, member.getId());

            // then
            assertThat(dailyDietResponse).isNotNull();
            assertThat(dailyDietResponse)
                .extracting("uploadDate", "targetDietType")
                .contains(uploadDate, DietType.DIET);

            List<DietInfoResponse> dietInfoResponses = dailyDietResponse.getDietInfos();
            assertThat(dietInfoResponses).hasSize(3)
                .extracting("dietCategory", "dietTime")
                .containsExactlyInAnyOrder(
                    tuple(BREAKFAST, LocalDateTime.of(2024, 9, 7, 8, 30)),
                    tuple(LUNCH, LocalDateTime.of(2024, 9, 7, 12, 0)),
                    tuple(DINNER, LocalDateTime.of(2024, 9, 7, 19, 20))
                );

            List<DietFoodResponse> dietFoodResponses1 = dailyDietResponse.getDietInfos().get(0).getDietFoods();
            assertThat(dietFoodResponses1).hasSize(2);

            List<DietFoodResponse> dietFoodResponses2 = dailyDietResponse.getDietInfos().get(1).getDietFoods();
            assertThat(dietFoodResponses2).hasSize(2);

            List<DietFoodResponse> dietFoodResponses3 = dailyDietResponse.getDietInfos().get(2).getDietFoods();
            assertThat(dietFoodResponses3).hasSize(2);
        }

        @DisplayName("요청 날짜에 저장된 식단 정보가 없다면 null 을 응답한다.")
        @Test
        void findDietByMemberAndUploadDateWhenDietIsEmpty() {
            // given
            final LocalDate uploadDate = LocalDate.of(2024, 9, 7);
            Member member = saveMember();

            // when
            DailyDietResponse dailyDietResponse = dietService.findDietByMemberAndUploadDate(uploadDate, member.getId());

            // then
            assertThat(dailyDietResponse).isNull();
        }
    }


    @DisplayName("식단 정보 ID로 단일 식단 정보를 조회할 수 있다.")
    @Test
    void findDietInfoById() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 22);
        LocalDateTime dietTime = LocalDateTime.of(uploadDate, LocalTime.of(8, 30));

        DietFood dietFood1 = createDietFood("음식1", 1200, 150, 200, 100);
        DietFood dietFood2 = createDietFood("음식2", 1200, 150, 200, 100);
        List<DietFood> dietFoods = List.of(dietFood1, dietFood2);

        Diets diets = saveDiets(member, uploadDate, List.of(dietFoods));
        DietInfo dietInfo = saveDietInfo(diets, BREAKFAST, dietTime, dietFoods);

        saveImage(member, "DIET_" + diets.getId() + "/DIETINFO_" + dietInfo.getId(), uploadDate);

        final Long memberId = member.getId();
        final Long dietInfoId = dietInfo.getId();

        // when
        DietInfoResponse dietInfoResponse = dietService.findDietInfoById(memberId, dietInfoId);

        // then
        assertThat(dietInfoResponse)
            .extracting("dietCategory", "dietTime", "totalCalorie", "totalProtein", "totalCarbohydrate", "totalFat")
            .contains(BREAKFAST, dietTime, 2400.0, 300.0, 400.0, 200.0);

        List<DietFoodResponse> dietFoodResponses = dietInfoResponse.getDietFoods();
        assertThat(dietFoodResponses).hasSize(2);

        List<ImageResponse> imageResponses = dietInfoResponse.getImages();
        assertThat(imageResponses).hasSize(1);
    }

    @DisplayName("식단 정보 ID로 조회 시 데이터가 없으면 null을 반환한다.")
    @Test
    void findDietInfoByIdWhenDietInfoIsEmpty() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 22);

        DietFood dietFood1 = createDietFood("음식1", 1200, 150, 200, 100);
        List<DietFood> dietFoods = List.of(dietFood1);

        Diets diets = saveDiets(member, uploadDate, List.of(dietFoods));
        saveDietInfo(diets, BREAKFAST, LocalDateTime.of(uploadDate, LocalTime.of(8, 30)), dietFoods);

        final Long memberId = member.getId();
        final Long dietInfoId = 2L;

        // when
        DietInfoResponse dietInfoResponse = dietService.findDietInfoById(memberId, dietInfoId);

        // then
        assertThat(dietInfoResponse).isNull();
    }

    @DisplayName("단일 식단 정보 수정하기")
    @Test
    void modifyDietInfo() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 22);

        DietFood dietFood1 = createDietFood("음식3", 3, 150, 200, 100);
        DietFood dietFood2 = createDietFood("음식4", 4, 150, 200, 100);
        List<DietFood> dietFoods = List.of(dietFood1, dietFood2);

        Diets savedDiets = saveDiets(member, uploadDate, List.of(dietFoods));
        DietInfo targetDietInfo = saveDietInfo(savedDiets, LUNCH, LocalDateTime.of(uploadDate, LocalTime.of(12, 0)), dietFoods);

        final Long memberId = member.getId();
        final Long dietId = savedDiets.getId();
        final Long dietInfoId = targetDietInfo.getId();

        DietFoodRequest dietFoodRequest1 = createaDietFoodRequest("sample1", 100, 10, 10, 10);
        DietFoodRequest dietFoodRequest2 = createaDietFoodRequest("sample2", 200, 20, 20, 20);

        EditDietInfoRequest request = EditDietInfoRequest.builder()
            .uploadDate(uploadDate)
            .dietCategory(DINNER)
            .dietTime(LocalTime.of(19, 0))
            .deletedFoodIds(List.of(dietFood2.getId()))
            .deletedImageIds(List.of())
            .dietFoods(List.of(dietFoodRequest1, dietFoodRequest2))
            .build();

        // when
        dietService.modifyDietInfo(memberId, dietId, dietInfoId, request, null);

        // then
        List<Diets> dietsList = dietRepository.findAll();
        assertThat(dietsList).isNotEmpty();

        Diets diets = dietsList.get(0);
        assertThat(diets)
            .extracting("totalCalorie", "totalCarbohydrate", "totalProtein", "totalFat")
            .contains(303.0, 180.0, 230.0, 130.0);

        List<DietInfo> dietInfos = dietInfoRepository.findAll();
        assertThat(dietInfos).hasSize(1);

        DietInfo dietInfo = dietInfos.get(0);
        assertThat(dietInfo)
            .extracting("dietCategory", "dietTime", "totalCalorie", "totalCarbohydrate", "totalProtein", "totalFat")
            .contains(DINNER, LocalDateTime.of(uploadDate, LocalTime.of(19, 0)), 303.0, 180.0, 230.0, 130.0);

        List<DietFood> dietFoodList = dietFoodRepository.findAll();
        assertThat(dietFoodList).hasSize(3)
            .extracting("name", "calories", "carbohydrate", "protein", "fat")
            .containsExactlyInAnyOrder(
                tuple("음식3", 3.0, 150.0, 200.0, 100.0),
                tuple("sample1", 100.0, 10.0, 10.0, 10.0),
                tuple("sample2", 200.0, 20.0, 20.0, 20.0)
            );
    }

    @DisplayName("단일 식단 정보 삭제하기")
    @Test
    void deleteDietInfo() {
        // given
        final LocalDate uploadDate = LocalDate.of(2024, 9, 7);

        Member member = saveMember();

        DietFood dietFood1 = createDietFood("음식1", 100, 150, 200, 100);
        DietFood dietFood2 = createDietFood("음식2", 100, 150, 200, 100);
        DietFood dietFood3 = createDietFood("음식3", 100, 150, 200, 100);
        DietFood dietFood4 = createDietFood("음식4", 100, 150, 200, 100);
        DietFood dietFood5 = createDietFood("음식5", 100, 150, 200, 100);
        DietFood dietFood6 = createDietFood("음식6", 100, 150, 200, 100);
        List<DietFood> dietFoods1 = List.of(dietFood1, dietFood2);
        List<DietFood> dietFoods2 = List.of(dietFood3, dietFood4);
        List<DietFood> dietFoods3 = List.of(dietFood5, dietFood6);

        Diets diets = saveDiets(member, uploadDate, List.of(dietFoods1, dietFoods2, dietFoods3));
        saveDietInfo(diets, BREAKFAST, LocalDateTime.of(2024, 9, 7, 8, 30), dietFoods1);
        saveDietInfo(diets, LUNCH, LocalDateTime.of(2024, 9, 7, 12, 0), dietFoods2);
        DietInfo dietInfo = saveDietInfo(diets, DINNER, LocalDateTime.of(2024, 9, 7, 19, 20), dietFoods3);

        // when
        dietService.deleteDietInfo(member.getId(), diets.getId(), dietInfo.getId());

        // then
        Diets findDiets = dietRepository.findById(diets.getId()).get();
        assertThat(findDiets)
            .extracting("totalCalorie", "totalCarbohydrate", "totalProtein", "totalFat")
            .contains(400.0, 600.0, 800.0, 400.0);

        List<DietInfo> dietInfos = dietInfoRepository.findAll();
        assertThat(dietInfos).hasSize(2);
    }

    private DietFoodRequest createaDietFoodRequest(String name, double calories, double carbohydrate, double protein, double fat) {
        return DietFoodRequest.builder()
            .name(name)
            .capacity(100)
            .units("g")
            .calories(calories)
            .carbohydrate(carbohydrate)
            .protein(protein)
            .fat(fat)
            .build();
    }

    private Diets saveDiets(Member member, LocalDate uploadDate, List<List<DietFood>> lists) {
        double totalCalorie = 0.0;
        double totalProtein = 0.0;
        double totalCarbohydrate = 0.0;
        double totalFat = 0.0;

        for (List<DietFood> list : lists) {
            DietNutritionalStatistics<DietFood> statistics = DietNutritionalStatistics.getStatisticsBy(list);
            totalCalorie += statistics.getTotalCalories();
            totalProtein += statistics.getTotalProtein();
            totalCarbohydrate += statistics.getTotalCarbohydrate();
            totalFat += statistics.getTotalFat();
        }
        return dietRepository.save(
            Diets.builder()
                .member(member)
                .uploadDate(uploadDate)
                .targetDietType(DietType.DIET)
                .totalCalorie(totalCalorie)
                .totalProtein(totalProtein)
                .totalCarbohydrate(totalCarbohydrate)
                .totalFat(totalFat)
                .build()
        );
    }

    private DietInfo saveDietInfo(Diets diets, DietCategory dietCategory, LocalDateTime dietTime, List<DietFood> dietFoods) {
        DietNutritionalStatistics<DietFood> statistics = DietNutritionalStatistics.getStatisticsBy(dietFoods);
        return dietInfoRepository.save(
            DietInfo.builder()
                .diets(diets)
                .dietCategory(dietCategory)
                .dietTime(dietTime)
                .totalCalorie(statistics.getTotalCalories())
                .totalProtein(statistics.getTotalProtein())
                .totalCarbohydrate(statistics.getTotalCarbohydrate())
                .totalFat(statistics.getTotalFat())
                .dietFoods(dietFoods)
                .build()
        );
    }

    private DietFood createDietFood(String name, double calories, double carbohydrate, double protein, double fat) {
        return DietFood.builder()
            .name(name)
            .capacity(100)
            .units("g")
            .calories(calories)
            .carbohydrate(carbohydrate)
            .protein(protein)
            .fat(fat)
            .build();
    }

    private Member saveMember() {
        return memberRepository.save(
            Member.builder()
                .email("test@test.com")
                .name("member1")
                .dietType(DietType.DIET)
                .role(Role.MEMBER)
                .build()
        );
    }

    private Image saveImage(Member member, String usageIdentificationId, LocalDate uploadDate) {
        return imageRepository.save(Image.builder()
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