package com.sideproject.withpt.application.diet.service;

import com.sideproject.withpt.application.diet.dto.request.DietRequest;
import com.sideproject.withpt.application.diet.dto.request.FoodItemRequest;
import com.sideproject.withpt.application.diet.exception.DietException;
import com.sideproject.withpt.application.diet.repository.DietRepository;
import com.sideproject.withpt.application.diet.repository.FoodItemRepository;
import com.sideproject.withpt.application.exercise.exception.ExerciseException;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Diets;
import com.sideproject.withpt.domain.record.FoodItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DietService {

    private final DietRepository dietRepository;
    private final MemberRepository memberRepository;
    private final FoodItemRepository foodItemRepository;

    @Transactional
    public void saveDiet(Long memberId, DietRequest request) {
        Member member = validateMemberId(memberId);
        Diets diets = request.toEntity(member);
        dietRepository.save(diets);
    }

    @Transactional
    public void modifyDiet(Long memberId, Long dietId, DietRequest request) {
        Diets diets = validateDietId(dietId, memberId);
        List<FoodItem> foodItems = foodItemRepository.findByDietsId(diets.getId());

        for (FoodItemRequest foodItem : request.getFoodItems()) {

        }

        diets.updateDiets(request);
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

}
