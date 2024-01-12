package com.sideproject.withpt.application.education.controller;

import com.sideproject.withpt.application.education.controller.reponse.EducationResponse;
import com.sideproject.withpt.application.education.controller.request.EducationEditRequest;
import com.sideproject.withpt.application.education.controller.request.EducationSaveRequest;
import com.sideproject.withpt.application.education.service.EducationQueryService;
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
@RequestMapping("/api/v1/trainers/{trainerId}/educations")
public class EducationController {

    private final EducationQueryService educationQueryService;

    @Operation(summary = "트레이너 모든 교육내역 조회")
    @GetMapping
    public ApiSuccessResponse<Slice<EducationResponse>> getAllEducations(@PathVariable Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            educationQueryService.getAllEducations(trainerId, pageable)
        );
    }

    @Operation(summary = "트레이너 교육내역 단건 조회")
    @GetMapping("/{educationId}")
    public ApiSuccessResponse<EducationResponse> getEducation(@PathVariable Long trainerId, @PathVariable Long educationId) {
        return ApiSuccessResponse.from(
            educationQueryService.getEducation(trainerId, educationId)
        );
    }

    @Operation(summary = "트레이너 교육내역 추가")
    @PostMapping
    public ApiSuccessResponse<EducationResponse> saveEducation(@PathVariable Long trainerId, @Valid @RequestBody EducationSaveRequest request) {
        return ApiSuccessResponse.from(
            educationQueryService.saveEducation(trainerId, request.toEntity())
        );
    }

    @Operation(summary = "교육내역 수정")
    @PatchMapping
    public ApiSuccessResponse<EducationResponse> editEducation(@PathVariable Long trainerId, @Valid @RequestBody EducationEditRequest request) {
        return ApiSuccessResponse.from(
            educationQueryService.editEducation(trainerId, request)
        );
    }

    @Operation(summary = "교육내역 삭제")
    @DeleteMapping("/{educationId}")
    public void deleteEducation(@PathVariable Long trainerId, @PathVariable Long educationId) {
        educationQueryService.deleteEducation(trainerId, educationId);
    }
}
