package com.sideproject.withpt.application.record.diet.controller;

import com.sideproject.withpt.application.record.diet.controller.request.EditDietInfoRequest;
import com.sideproject.withpt.application.record.diet.controller.request.SaveDietRequest;
import com.sideproject.withpt.application.record.diet.exception.DietException;
import com.sideproject.withpt.application.record.diet.service.DietService;
import com.sideproject.withpt.application.record.diet.service.response.DailyDietResponse;
import com.sideproject.withpt.application.record.diet.service.response.DietInfoResponse;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/record/diets")
public class DietController {

    private final DietService dietService;

    @Operation(summary = "날짜별 식단 조회")
    @GetMapping
    public ApiSuccessResponse<DailyDietResponse> findDietByUploadDate(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate uploadDate,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(
            dietService.findDietByMemberAndUploadDate(uploadDate, memberId)
        );
    }

    @Operation(summary = "식단 입력하기")
    @PostMapping
    public void saveDiet(@Valid @RequestPart SaveDietRequest request, @RequestPart(required = false) List<MultipartFile> files,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        if (request.getDietFoods() == null || request.getDietFoods().isEmpty()) {
            throw DietException.AT_LEAST_ONE_DIET_DATA_MUST_BE_INCLUDED;
        }
        dietService.saveOrUpdateDiet(memberId, request, files);
    }

    @Operation(summary = "식단 정보 조회")
    @GetMapping("/{dietId}/dietInfos/{dietInfoId}")
    public ApiSuccessResponse<DietInfoResponse> findDietInfoById(@PathVariable Long dietId, @PathVariable Long dietInfoId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(
            dietService.findDietInfoById(memberId, dietInfoId)
        );
    }

    @Operation(summary = "식단 정보 수정하기")
    @PatchMapping("/{dietId}/dietInfos/{dietInfoId}")
    public void modifyDiet(@Valid @RequestPart EditDietInfoRequest request, @PathVariable Long dietId, @PathVariable Long dietInfoId,
        @RequestPart(required = false) List<MultipartFile> files,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        dietService.modifyDietInfo(memberId, dietId, dietInfoId, request, files);
    }

    @Operation(summary = "식단 정보 삭제하기")
    @DeleteMapping("/{dietId}/dietInfos/{dietInfoId}")
    public void deleteDietInfo(@PathVariable Long dietId, @PathVariable Long dietInfoId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        dietService.deleteDietInfo(memberId, dietId, dietInfoId);
    }

}
