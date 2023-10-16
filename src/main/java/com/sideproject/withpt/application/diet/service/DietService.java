package com.sideproject.withpt.application.diet.service;

import com.sideproject.withpt.application.diet.dto.request.DietRequest;
import com.sideproject.withpt.application.diet.dto.request.FoodItemRequest;
import com.sideproject.withpt.application.diet.repository.DietRepository;
import com.sideproject.withpt.application.diet.repository.FoodItemRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Diets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DietService {

    private final DietRepository dietRepository;
    private final MemberRepository memberRepository;
    private final FoodItemRepository foodItemRepository;

    // 식단 입력하기
    @Transactional
    public void saveDiet(Long memberId, DietRequest request) {
        Member member = validateMemberId(memberId);
        Diets diets = request.toEntity(member);

        dietRepository.save(diets);

        for (FoodItemRequest food : request.getFoodItems()) {
            foodItemRepository.save(food.toEntity(diets));
        }
    }

    // 식단 수정하기

    // 식단 삭제하기

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> GlobalException.TEST_ERROR);
    }

}
