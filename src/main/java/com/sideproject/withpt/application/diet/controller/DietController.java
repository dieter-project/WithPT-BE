package com.sideproject.withpt.application.diet.controller;

import com.sideproject.withpt.application.diet.controller.request.SaveDietRequest;
import com.sideproject.withpt.application.diet.controller.response.DailyDietResponse;
import com.sideproject.withpt.application.diet.service.DietService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/diets")
public class DietController {

    private final DietService dietService;

    @Operation(summary = "날짜별 식단 조회")
    @GetMapping
    public ApiSuccessResponse<DailyDietResponse> findDiet(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate uploadDate,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(
            dietService.findDietByUploadDate(uploadDate, memberId)
        );
    }

    @Operation(summary = "식단 입력하기")
    @PostMapping
    public void saveDiet(@Valid @RequestPart SaveDietRequest request, @RequestPart(required = false) List<MultipartFile> files,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        dietService.saveDiet(memberId, request, files);
    }

    @Operation(summary = "식단 수정하기")
    @PatchMapping("/{dietsId}")
    public void modifyDiet(@Valid @RequestBody SaveDietRequest request, @PathVariable Long dietsId,
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
