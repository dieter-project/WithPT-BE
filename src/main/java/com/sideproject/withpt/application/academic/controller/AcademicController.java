package com.sideproject.withpt.application.academic.controller;

import com.sideproject.withpt.application.academic.controller.request.AcademicEditRequest;
import com.sideproject.withpt.application.academic.controller.request.AcademicSaveRequest;
import com.sideproject.withpt.application.academic.service.response.AcademicResponse;
import com.sideproject.withpt.application.academic.service.AcademicService;
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
@RequestMapping("/api/v1/trainers/{trainerId}/academics")
public class AcademicController {

    private final AcademicService academicService;

    @Operation(summary = "트레이너 모든 학력 조회")
    @GetMapping
    public ApiSuccessResponse<Slice<AcademicResponse>> getAllAcademics(@PathVariable Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            academicService.getAllAcademics(trainerId, pageable)
        );
    }

    @Operation(summary = "트레이너 학력 단건 조회")
    @GetMapping("/{academicId}")
    public ApiSuccessResponse<AcademicResponse> getAcademic(@PathVariable Long trainerId, @PathVariable Long academicId) {
        return ApiSuccessResponse.from(
            academicService.getAcademic(trainerId, academicId)
        );
    }

    @Operation(summary = "트레이너 학력 추가")
    @PostMapping
    public ApiSuccessResponse<AcademicResponse> saveAcademic(@PathVariable Long trainerId, @Valid @RequestBody AcademicSaveRequest request) {
        return ApiSuccessResponse.from(
            academicService.saveAcademic(trainerId, request.toEntity())
        );
    }

    @Operation(summary = "학력 사항 수정")
    @PatchMapping
    public ApiSuccessResponse<AcademicResponse> editAcademic(@PathVariable Long trainerId, @Valid @RequestBody AcademicEditRequest request) {
        return ApiSuccessResponse.from(
            academicService.editAcademic(trainerId, request)
        );
    }

    @Operation(summary = "학력 삭제")
    @DeleteMapping("/{academicId}")
    public void deleteAcademic(@PathVariable Long trainerId, @PathVariable Long academicId) {
        academicService.deleteAcademic(trainerId, academicId);
    }
}
