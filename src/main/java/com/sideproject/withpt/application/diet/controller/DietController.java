package com.sideproject.withpt.application.diet.controller;

import com.sideproject.withpt.application.diet.dto.request.DietRequest;
import com.sideproject.withpt.application.diet.dto.response.DietResponse;
import com.sideproject.withpt.application.diet.service.DietService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/diet")
public class DietController {

    private final DietService dietService;

    @Operation(summary = "식단 단건 조회하기")
    @GetMapping("/{dietId}")
    public ApiSuccessResponse<DietResponse> findOneDiet(@PathVariable Long dietId,
                                                        @AuthenticationPrincipal Long memberId) {
        return null;
    }

    @Operation(summary = "식단 입력하기")
    @PostMapping
    public void saveDiet(@Valid @RequestPart(value = "dto") DietRequest request,
                         @RequestPart(value = "files", required = false) List<MultipartFile> files,
                         @AuthenticationPrincipal Long memberId) {
        dietService.saveDiet(memberId, request, files);
    }

    @Operation(summary = "식단 수정하기")
    @PatchMapping("/{dietId}")
    public void modifyDiet(@Valid @RequestPart(value = "dto") DietRequest request,
                           @RequestPart(value = "files", required = false) List<MultipartFile> files,
                           @PathVariable Long dietId, @AuthenticationPrincipal Long memberId) {
        dietService.modifyDiet(memberId, dietId, request, files);
    }

    @Operation(summary = "식단 삭제하기")
    @DeleteMapping("/{dietId}")
    public void deleteDiet(@PathVariable Long dietId, @AuthenticationPrincipal Long memberId) {
        dietService.deleteDiet(memberId, dietId);
    }

}
