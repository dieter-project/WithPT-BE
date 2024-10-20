package com.sideproject.withpt.application.award.controller;

import com.sideproject.withpt.application.award.controller.reponse.AwardResponse;
import com.sideproject.withpt.application.award.controller.request.AwardEditRequest;
import com.sideproject.withpt.application.award.controller.request.AwardSaveRequest;
import com.sideproject.withpt.application.award.service.AwardService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trainers/{trainerId}/awards")
public class AwardController {

    private final AwardService awardService;

    @Operation(summary = "트레이너 모든 수상내역 조회")
    @GetMapping
    public ApiSuccessResponse<Slice<AwardResponse>> getAllAwards(@PathVariable Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            awardService.getAllAwards(trainerId, pageable)
        );
    }

    @Operation(summary = "트레이너 수상내역 단건 조회")
    @GetMapping("/{awardId}")
    public ApiSuccessResponse<AwardResponse> getAward(@PathVariable Long trainerId, @PathVariable Long awardId) {
        return ApiSuccessResponse.from(
            awardService.getAward(trainerId, awardId)
        );
    }

    @Operation(summary = "트레이너 수상내역 추가")
    @PostMapping
    public ApiSuccessResponse<AwardResponse> saveAward(@PathVariable Long trainerId, @Valid @RequestBody AwardSaveRequest request) {
        return ApiSuccessResponse.from(
           awardService.saveAward(trainerId, request.toEntity())
        );
    }

    @Operation(summary = "수상내역 수정")
    @PatchMapping
    public ApiSuccessResponse<AwardResponse> editAward(@PathVariable Long trainerId, @Valid @RequestBody AwardEditRequest request) {
        return ApiSuccessResponse.from(
            awardService.editAward(trainerId, request)
        );
    }

    @Operation(summary = "수상내역 삭제")
    @DeleteMapping("/{awardId}")
    public void deleteAward(@PathVariable Long trainerId, @PathVariable Long awardId) {
        awardService.deleteAward(trainerId, awardId);
    }
}
