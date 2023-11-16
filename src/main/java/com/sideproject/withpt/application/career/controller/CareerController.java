package com.sideproject.withpt.application.career.controller;

import com.sideproject.withpt.application.career.controller.request.CareerEditRequest;
import com.sideproject.withpt.application.career.controller.request.CareerSaveRequest;
import com.sideproject.withpt.application.career.controller.response.CareerResponse;
import com.sideproject.withpt.application.career.service.CareerQueryService;
import com.sideproject.withpt.application.trainer.controller.request.CareerRequest;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/v1/trainers/mypage/management/careers")
public class CareerController {

    private final CareerQueryService careerQueryService;

    @Operation(summary = "트레이너 모든 경력 조회")
    @GetMapping
    public ApiSuccessResponse<Slice<CareerResponse>> getAllCareers(@AuthenticationPrincipal Long trainerId,
        Pageable pageable) {
        return ApiSuccessResponse.from(
            careerQueryService.getAllCareers(trainerId, pageable)
        );
    }

    @Operation(summary = "트레이너 경력 단건 조회")
    @GetMapping("/{careerId}")
    public ApiSuccessResponse<CareerResponse> getCareer(@AuthenticationPrincipal Long trainerId, @PathVariable Long careerId) {
        return ApiSuccessResponse.from(
          careerQueryService.getCareer(trainerId, careerId)
        );
    }

    @Operation(summary = "트레이너 경력 추가")
    @PostMapping
    public ApiSuccessResponse<CareerResponse> saveCareer(@AuthenticationPrincipal Long trainerId, @RequestBody CareerSaveRequest request) {
        return ApiSuccessResponse.from(
            careerQueryService.saveCareer(trainerId, request.toEntity())
        );
    }

    @Operation(summary = "경력 사항 수정")
    @PatchMapping
    public ApiSuccessResponse<CareerResponse> editCareer(@AuthenticationPrincipal Long trainerId, @Valid @RequestBody CareerEditRequest request) {
        return ApiSuccessResponse.from(
            careerQueryService.editCareer(trainerId, request)
        );
    }

    @Operation(summary = "경력 삭제")
    @DeleteMapping("/{careerId}")
    public void deleteCareer(@AuthenticationPrincipal Long trainerId, @PathVariable Long careerId) {
        careerQueryService.deleteCareer(trainerId, careerId);
    }
}
