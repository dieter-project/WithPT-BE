package com.sideproject.withpt.application.diet.service;

import com.sideproject.withpt.application.Food.repository.FoodRepository;
import com.sideproject.withpt.application.diet.dto.request.DietRequest;
import com.sideproject.withpt.application.diet.dto.request.FoodItemRequest;
import com.sideproject.withpt.application.diet.exception.DietException;
import com.sideproject.withpt.application.diet.repository.DietRepository;
import com.sideproject.withpt.application.diet.repository.FoodItemRepository;
import com.sideproject.withpt.application.exercise.exception.ExerciseException;
import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Diet;
import com.sideproject.withpt.domain.record.Food;
import com.sideproject.withpt.domain.record.FoodItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DietService {

    private final DietRepository dietRepository;
    private final MemberRepository memberRepository;
    private final FoodRepository foodRepository;
    private final FoodItemRepository foodItemRepository;
    private final ImageRepository imageRepository;

    private final ImageUploader imageUploader;

    @Transactional
    public void saveDiet(Long memberId, DietRequest request, List<MultipartFile> file) {
        Member member = validateMemberId(memberId);

        if (request.getFoodItems() == null || request.getFoodItems().isEmpty()) {
            throw DietException.DIET_FOOD_NOT_EXIST;
        }

        Diet diet = dietRepository.save(request.toEntity(member));
        saveFoodItem(request, diet);
        saveDietImage(file, member, diet);
    }

    @Transactional
    public void modifyDiet(Long memberId, Long dietsId, DietRequest request, List<MultipartFile> file) {
        Diet diet = validateDietId(dietsId, memberId);

        foodItemRepository.deleteByDietId(dietsId);
        imageRepository.deleteByEntityId(dietsId);

        // 식단 상세 & 이미지 삭제하고 재저장
        if (request.getFoodItems() != null) saveFoodItem(request, diet);
        saveDietImage(file, validateMemberId(memberId), diet);

        diet.updateDiets(request);
    }

    @Transactional
    public void deleteDiet(Long memberId, Long dietId) {
        validateDietId(dietId, memberId);
        dietRepository.deleteById(dietId);
    }

    private void saveFoodItem(DietRequest request, Diet diet) {
        for (FoodItemRequest foodItemRequest : request.getFoodItems()) {
            Food food = foodRepository.findById(foodItemRequest.getId()).orElseThrow(() -> DietException.DIET_FOOD_NOT_EXIST);
            int gramRatio = foodItemRequest.getGram() / food.getTotalGram();

            foodItemRepository.save(
                    FoodItem.builder()
                            .food(food)
                            .diet(diet)
                            .gram(foodItemRequest.getGram())
                            .calories(gramRatio * food.getCalories())
                            .carbohydrate(gramRatio * food.getCarbohydrate())
                            .protein(gramRatio * food.getProtein())
                            .fat(gramRatio * food.getFat())
                            .build());
        }
    }

    private void saveDietImage(List<MultipartFile> file, Member member, Diet diet) {
        if(file != null && file.size() > 0) {
            imageUploader.uploadAndSaveImages(file, diet.getId(), Usages.MEAL, member);
        }
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> GlobalException.TEST_ERROR);
    }

    private Diet validateDietId(Long exerciseId, Long memberId) {
        Diet diet = dietRepository.findById(exerciseId)
                .orElseThrow(() -> DietException.DIET_NOT_EXIST);
        Member member = validateMemberId(memberId);

        if (!diet.getMember().getId().equals(member.getId())) {
            throw ExerciseException.EXERCISE_NOT_BELONG_TO_MEMBER;
        }
        return diet;
    }

}
