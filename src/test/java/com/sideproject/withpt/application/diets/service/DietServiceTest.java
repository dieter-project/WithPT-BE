package com.sideproject.withpt.application.diets.service;

import com.sideproject.withpt.application.diet.dto.request.DietRequest;
import com.sideproject.withpt.application.diet.dto.request.FoodItemRequest;
import com.sideproject.withpt.application.diet.repository.DietRepository;
import com.sideproject.withpt.application.diet.repository.FoodItemRepository;
import com.sideproject.withpt.application.diet.service.DietService;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.config.TestEmbeddedRedisConfig;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Diet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@Import(TestEmbeddedRedisConfig.class)
public class DietServiceTest {

    @Mock
    private DietRepository dietRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FoodItemRepository foodItemRepository;

    @InjectMocks
    private DietService dietService;

    @Test
    @DisplayName("식단 기록 저장하기")
    void saveDiet() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("식단 기록 수정하기")
    void modifyDiet() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("식단 기록 삭제하기")
    void deleteDiet() {
        // given

        // when

        // then
    }

    private DietRequest createAddDietRequest() {
        FoodItemRequest foodItem = FoodItemRequest.builder()
                .id(1L)
                .gram(100)
                .build();

        return DietRequest.builder()
                .mealCategory(MealCategory.BREAKFAST)
                .mealTime(LocalDateTime.now())
                .foodItems(List.of(foodItem))
                .build();
    }

    private Member createMember() {
        return Member.builder()
                .id(1L)
                .nickname("test")
                .build();
    }


}
