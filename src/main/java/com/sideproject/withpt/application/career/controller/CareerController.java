package com.sideproject.withpt.application.career.controller;

import com.sideproject.withpt.application.career.controller.request.CareerEditRequest;
import com.sideproject.withpt.application.career.controller.request.CareerSaveRequest;
import com.sideproject.withpt.application.career.service.response.CareerResponse;
import com.sideproject.withpt.application.career.service.CareerService;
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
@RequestMapping("/api/v1/trainers/{trainerId}/careers")
public class CareerController {

    private final CareerService careerService;

    @Operation(summary = "트레이너 모든 경력 조회")
    @GetMapping
    public ApiSuccessResponse<Slice<CareerResponse>> getAllCareers(@PathVariable Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            careerService.getAllCareers(trainerId, pageable)
        );
    }

    @Operation(summary = "트레이너 경력 단건 조회")
    @GetMapping("/{careerId}")
    public ApiSuccessResponse<CareerResponse> getCareer(@PathVariable Long trainerId, @PathVariable Long careerId) {
        return ApiSuccessResponse.from(
          careerService.getCareer(trainerId, careerId)
        );
    }

    @Operation(summary = "트레이너 경력 추가")
    @PostMapping
    public ApiSuccessResponse<CareerResponse> saveCareer(@PathVariable Long trainerId, @RequestBody CareerSaveRequest request) {
        return ApiSuccessResponse.from(
            careerService.saveCareer(trainerId, request.toEntity())
        );
    }

    @Operation(summary = "경력 사항 수정")
    @PatchMapping
    public ApiSuccessResponse<CareerResponse> editCareer(@PathVariable Long trainerId, @Valid @RequestBody CareerEditRequest request) {
        return ApiSuccessResponse.from(
            careerService.editCareer(trainerId, request)
        );
    }

    @Operation(summary = "경력 삭제")
    @DeleteMapping("/{careerId}")
    public void deleteCareer(@PathVariable Long trainerId, @PathVariable Long careerId) {
        careerService.deleteCareer(trainerId, careerId);
    }
}
