package com.sideproject.withpt.application.certificate.controller;

import com.sideproject.withpt.application.academic.controller.request.AcademicEditRequest;
import com.sideproject.withpt.application.academic.controller.request.AcademicSaveRequest;
import com.sideproject.withpt.application.certificate.controller.reponse.CertificateResponse;
import com.sideproject.withpt.application.certificate.controller.request.CertificateEditRequest;
import com.sideproject.withpt.application.certificate.controller.request.CertificateSaveRequest;
import com.sideproject.withpt.application.certificate.service.CertificateQueryService;
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
@RequestMapping("/api/v1/trainers/mypage/management/certificates")
public class CertificateController {

    private final CertificateQueryService certificateQueryService;

    @Operation(summary = "트레이너 모든 자격증 조회")
    @GetMapping
    public ApiSuccessResponse<Slice<CertificateResponse>> getAllCertificates(@AuthenticationPrincipal Long trainerId,
        Pageable pageable) {
        return ApiSuccessResponse.from(
            certificateQueryService.getAllCertificate(trainerId, pageable)
        );
    }

    @Operation(summary = "트레이너 자격증 단건 조회")
    @GetMapping("/{certificateId}")
    public ApiSuccessResponse<CertificateResponse> getCertificate(@AuthenticationPrincipal Long trainerId, @PathVariable Long certificateId) {
        return ApiSuccessResponse.from(
            certificateQueryService.getCertificate(trainerId, certificateId)
        );
    }

    @Operation(summary = "트레이너 자격증 추가")
    @PostMapping
    public ApiSuccessResponse<CertificateResponse> saveCertificate(@AuthenticationPrincipal Long trainerId, @RequestBody CertificateSaveRequest request) {
        return ApiSuccessResponse.from(
            certificateQueryService.saveCertificate(trainerId, request)
        );
    }

    @Operation(summary = "자격증 수정")
    @PatchMapping
    public ApiSuccessResponse<CertificateResponse> editCertificate(@AuthenticationPrincipal Long trainerId, @Valid @RequestBody CertificateEditRequest request) {
        return ApiSuccessResponse.from(
            certificateQueryService.editCertificate(trainerId, request)
        );
    }

    @Operation(summary = "자격증 삭제")
    @DeleteMapping("/{certificateId}")
    public void deleteCertificate(@AuthenticationPrincipal Long trainerId, @PathVariable Long certificateId) {
        certificateQueryService.deleteCertificate(trainerId, certificateId);
    }
}
