package com.sideproject.withpt.application.diet.service;

import com.sideproject.withpt.application.diet.controller.request.DietFoodRequest;
import com.sideproject.withpt.application.diet.controller.request.EditDietInfoRequest;
import com.sideproject.withpt.application.diet.controller.request.SaveDietRequest;
import com.sideproject.withpt.application.diet.controller.request.SaveDietRequest.Summary;
import com.sideproject.withpt.application.diet.controller.response.DailyDietResponse;
import com.sideproject.withpt.application.diet.controller.response.DietInfoResponse;
import com.sideproject.withpt.application.diet.exception.DietException;
import com.sideproject.withpt.application.diet.repository.DietFoodRepository;
import com.sideproject.withpt.application.diet.repository.DietInfoRepository;
import com.sideproject.withpt.application.diet.repository.DietQueryRepository;
import com.sideproject.withpt.application.diet.repository.DietRepository;
import com.sideproject.withpt.application.exercise.exception.ExerciseException;
import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.diet.DietFood;
import com.sideproject.withpt.domain.record.diet.DietInfo;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DietService {

    private final DietRepository dietRepository;
    private final DietInfoRepository dietInfoRepository;
    private final DietFoodRepository dietFoodRepository;
    private final DietQueryRepository dietQueryRepository;
    private final MemberRepository memberRepository;
    private final ImageUploader imageUploader;

    @Transactional
    public void saveDiet(Long memberId, SaveDietRequest request, List<MultipartFile> files) {
        Member member = validateMemberId(memberId);

        if (request.getDietFoods() == null || request.getDietFoods().isEmpty()) {
            throw DietException.DIET_FOOD_NOT_EXIST;
        }

        dietQueryRepository.findByMemberAndUploadDate(member, request.getUploadDate())
            .ifPresentOrElse(diets -> {
                    Summary summary = request.getDietFoods().stream().collect(
                        Summary::new,
                        Summary::accept,
                        Summary::combine
                    );

                    diets.addTotalCalorie(summary.getTotalCalories());
                    diets.addTotalCarbohydrate(summary.getTotalCarbohydrate());
                    diets.addTotalProtein(summary.getTotalProtein());
                    diets.addTotalFat(summary.getTotalFat());

                    addDietInfo(request, files, member, diets);
                },
                () -> {
                    Diets saveDiets = dietRepository.save(request.toEntity(member));
                    addDietInfo(request, files, member, saveDiets);
                });
    }

    public DailyDietResponse findDietByUploadDate(LocalDate uploadDate, Long memberId) {
        Member member = validateMemberId(memberId);
        return dietQueryRepository.findDietByUploadDate(member, uploadDate);
    }

    public DietInfoResponse findDietInfoById(Long memberId, Long dietInfoId) {
        Member member = validateMemberId(memberId);
        return dietQueryRepository.findDietInfoById(member, dietInfoId);
    }

    @Transactional
    public void modifyDiet(Long memberId, Long dietsId, SaveDietRequest request) {
//        Diets diets = validateDietId(dietsId, memberId);
//
//        foodItemRepository.deleteByDiets(dietsId);
//
//        // 식단 음식 데이터 재저장
//        if (request.getDietFoods() != null) {
//            for (DietFoodRequest foodItemRequest : request.getDietFoods()) {
//                DietFood foodItem = foodItemRequest.toEntity(diets);
//
//                foodItemRepository.save(foodItem);
//                diets.addDietFood(foodItem);
//            }
//        }

//        diets.updateDiets(request);
    }

    @Transactional
    public void modifyDietInfo(Long memberId, Long dietId, Long dietInfoId, EditDietInfoRequest request, List<MultipartFile> files) {
        Member member = validateMemberId(memberId);
        Diets diets = dietRepository.findById(dietId)
            .orElseThrow(() -> DietException.DIET_NOT_EXIST);

        DietInfo dietInfo = dietInfoRepository.findById(dietInfoId)
            .orElseThrow(() -> DietException.DIET_FOOD_NOT_EXIST);

        // 삭제되는 음식 성분 값 빼기
        if (!request.getDeletedFoodIds().isEmpty()) {
            List<DietFood> deletedFoods = dietFoodRepository.findAllByIdInAndDietInfo(request.getDeletedFoodIds(), dietInfo);

            for (DietFood deletedFood : deletedFoods) {
                diets.subtractTotalCalorie(deletedFood.getCalories());
                diets.subtractTotalCarbohydrate(deletedFood.getCarbohydrate());
                diets.subtractTotalProtein(deletedFood.getProtein());
                diets.subtractTotalFat(deletedFood.getFat());

                dietInfo.subtractTotalCalorie(deletedFood.getCalories());
                dietInfo.subtractTotalCarbohydrate(deletedFood.getCarbohydrate());
                dietInfo.subtractTotalProtein(deletedFood.getProtein());
                dietInfo.subtractTotalFat(deletedFood.getFat());
            }

            for (DietFood dietFood : deletedFoods) {
                log.info("음식들 {}", dietFood);
            }
            dietFoodRepository.deleteAllByIdInAndDietInfo(request.getDeletedFoodIds(), dietInfo);
        }

        // 음식 추가
        if (!request.getDietFoods().isEmpty()) {
            Summary summary = request.getDietFoods().stream().collect(
                Summary::new,
                Summary::accept,
                Summary::combine
            );

            for (DietFoodRequest foodRequest : request.getDietFoods()) {
                DietFood dietFood = foodRequest.toEntity(dietInfo);
                dietInfo.addDietFood(dietFood);
            }

            diets.addTotalCalorie(summary.getTotalCalories());
            diets.addTotalCarbohydrate(summary.getTotalCarbohydrate());
            diets.addTotalProtein(summary.getTotalProtein());
            diets.addTotalFat(summary.getTotalFat());

            dietInfo.addTotalCalorie(summary.getTotalCalories());
            dietInfo.addTotalCarbohydrate(summary.getTotalCarbohydrate());
            dietInfo.addTotalProtein(summary.getTotalProtein());
            dietInfo.addTotalFat(summary.getTotalFat());
        }

        // 이미지 삭제
        if (!request.getDeletedImageIds().isEmpty()) {
            request.getDeletedImageIds().forEach(id -> imageUploader.deleteImage(id));
        }

        // 이미지 추가
        if (!files.isEmpty()) {
            imageUploader.uploadAndSaveImages(files, Usages.MEAL,
                "DIET_" + diets.getId() + "/DIETINFO_" + dietInfo.getId(), member);
        }

        dietInfo.setMealTime(LocalDateTime.of(request.getUploadDate(), request.getMealTime()));
        dietInfo.setMealCategory(request.getMealCategory());
    }

    @Transactional
    public void deleteDiet(Long memberId, Long dietId) {
        validateDietId(dietId, memberId);
        dietRepository.deleteById(dietId);
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.TEST_ERROR);
    }

    private Diets validateDietId(Long exerciseId, Long memberId) {
        Diets diets = dietRepository.findById(exerciseId)
            .orElseThrow(() -> DietException.DIET_NOT_EXIST);
        Member member = validateMemberId(memberId);

        if (!diets.getMember().getId().equals(member.getId())) {
            throw ExerciseException.EXERCISE_NOT_BELONG_TO_MEMBER;
        }
        return diets;
    }

    private void addDietInfo(SaveDietRequest request, List<MultipartFile> files, Member member, Diets diets) {

        Summary summary = request.getDietFoods().stream().collect(
            Summary::new,
            Summary::accept,
            Summary::combine
        );

        DietInfo dietInfo = DietInfo.builder()
            .diets(diets)
            .mealCategory(request.getMealCategory())
            .mealTime(LocalDateTime.of(request.getUploadDate(), request.getMealTime()))
            .totalCalorie(summary.getTotalCalories())
            .totalCarbohydrate(summary.getTotalCarbohydrate())
            .totalProtein(summary.getTotalProtein())
            .totalFat(summary.getTotalFat())
            .build();

        // 식단 상세 추가하기
        for (DietFoodRequest foodRequest : request.getDietFoods()) {
            DietFood dietFood = foodRequest.toEntity(dietInfo);
            dietInfo.addDietFood(dietFood);
        }
        DietInfo saveDietInfo = dietInfoRepository.save(dietInfo);
        imageUploader.uploadAndSaveImages(files, Usages.MEAL,
            "DIET_" + diets.getId() + "/DIETINFO_" + saveDietInfo.getId(),
            member);
    }

}
