package com.sideproject.withpt.application.diet.controller;

import com.sideproject.withpt.application.diet.dto.request.DietRequest;
import com.sideproject.withpt.application.diet.dto.response.DietResponse;
import com.sideproject.withpt.application.diet.service.DietService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/diet")
public class DietController {

    private final DietService dietService;

    // 식단 단건 조회하기
    @GetMapping("/{dietId}")
    public ApiSuccessResponse<DietResponse> findOneDiet(@PathVariable Long dietId, @AuthenticationPrincipal Long memberId) {
        return null;
    }

    // 식단 입력하기
    @PostMapping
    public ApiSuccessResponse saveDiet(@Valid @RequestBody DietRequest request, @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

    // 식단 수정하기
    @PatchMapping("/{dietId}")
    public ApiSuccessResponse modifyDiet(@Valid @RequestBody DietRequest request, @PathVariable Long dietId, @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

    // 식단 삭제하기
    @DeleteMapping("/{dietId}")
    public ApiSuccessResponse deleteDiet(@PathVariable Long dietId, @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

}
