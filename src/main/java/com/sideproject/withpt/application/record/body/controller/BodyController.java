package com.sideproject.withpt.application.record.body.controller;

import com.sideproject.withpt.application.record.body.controller.request.BodyInfoRequest;
import com.sideproject.withpt.application.record.body.controller.request.DeleteBodyImageRequest;
import com.sideproject.withpt.application.record.body.controller.request.WeightInfoRequest;
import com.sideproject.withpt.application.record.body.controller.response.BodyImageInfoResponse;
import com.sideproject.withpt.application.record.body.controller.response.WeightInfoResponse;
import com.sideproject.withpt.application.record.body.service.BodyService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/v1/members/record/body-info")
public class BodyController {

    private final BodyService bodyService;

    @Operation(summary = "해당 날짜의 체중 및 신체 정보 조회하기")
    @GetMapping
    public ApiSuccessResponse<WeightInfoResponse> findWeight(
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate uploadDate) {
        return ApiSuccessResponse.from(
            bodyService.findWeightInfo(memberId, uploadDate)
        );
    }

    @Operation(summary = "체중 입력하기")
    @PostMapping("/weight")
    public void saveWeight(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId,
        @Valid @RequestBody WeightInfoRequest request) {
        bodyService.saveWeight(memberId, request);
    }

    @Operation(summary = "신체 정보 입력하기")
    @PostMapping("/bodyInfo")
    public void saveBodyInfo(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId,
        @Valid @RequestBody BodyInfoRequest request) {
        bodyService.saveBodyInfo(memberId, request);
    }

    @Operation(summary = "회원의 전체 눈바디 히스토리 조회")
    @GetMapping("/images")
    public ApiSuccessResponse<Slice<BodyImageInfoResponse>> findAllBodyImage(
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId, Pageable pageable) {
        return ApiSuccessResponse.from(
            bodyService.findAllBodyImage(memberId, null, pageable)
        );
    }

    @Operation(summary = "해당하는 날짜의 눈바디 이미지 조회")
    @GetMapping("/image")
    public ApiSuccessResponse<Slice<BodyImageInfoResponse>> findTodayBodyImage(
        @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate uploadDate, Pageable pageable,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(
            bodyService.findAllBodyImage(memberId, uploadDate, pageable)
        );
    }

    @Operation(summary = "눈바디 이미지 업로드")
    @PostMapping("/image")
    public void saveBodyImage(
        @RequestPart(value = "files") List<MultipartFile> files, @RequestPart LocalDate uploadDate,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        bodyService.saveBodyImage(files, uploadDate, memberId);
    }

    @Operation(summary = "눈바디 이미지 삭제")
    @DeleteMapping("/image")
    public void deleteBodyImage(@RequestBody DeleteBodyImageRequest request,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        log.info("images {}", request.getImageIds());
        bodyService.deleteBodyImage(memberId, request);
    }

}
