package com.sideproject.withpt.application.diet.controller;

import com.sideproject.withpt.application.diet.dto.request.DietRequest;
import com.sideproject.withpt.application.diet.dto.response.DietResponse;
import com.sideproject.withpt.application.diet.service.DietService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "식단 단건 조회하기")
    @GetMapping("/{dietId}")
    public ApiSuccessResponse<DietResponse> findOneDiet(@PathVariable Long dietId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return null;
    }

    @Operation(summary = "식단 입력하기")
    @PostMapping
    public void saveDiet(@Valid @RequestBody DietRequest request,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        dietService.saveDiet(memberId, request);
    }

    @Operation(summary = "식단 수정하기")
    @PatchMapping("/{dietsId}")
    public void modifyDiet(@Valid @RequestBody DietRequest request, @PathVariable Long dietsId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        dietService.modifyDiet(memberId, dietsId, request);
    }

    @Operation(summary = "식단 삭제하기")
    @DeleteMapping("/{dietId}")
    public void deleteDiet(@PathVariable Long dietId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        dietService.deleteDiet(memberId, dietId);
    }

}
