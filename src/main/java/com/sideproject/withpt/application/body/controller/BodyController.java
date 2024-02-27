package com.sideproject.withpt.application.body.controller;

import com.sideproject.withpt.application.body.dto.request.BodyInfoRequest;
import com.sideproject.withpt.application.body.dto.request.WeightInfoRequest;
import com.sideproject.withpt.application.body.dto.response.BodyImageResponse;
import com.sideproject.withpt.application.body.dto.response.WeightInfoResponse;
import com.sideproject.withpt.application.body.service.BodyService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/body")
public class BodyController {

    private final BodyService bodyService;

    @Operation(summary = "해당 날짜의 체중 및 신체 정보 조회하기")
    @GetMapping
    public ApiSuccessResponse<WeightInfoResponse> findWeight(
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId, @RequestParam String dateTime) {
        LocalDate localDate = LocalDate.parse(dateTime, DateTimeFormatter.ISO_DATE);
        WeightInfoResponse weightInfo = bodyService.findWeightInfo(memberId, localDate);
        return ApiSuccessResponse.from(weightInfo);
    }

    @Operation(summary = "체중 입력하기")
    @PostMapping("/weight")
    public void saveWeight(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId,
        @Valid @RequestBody WeightInfoRequest request) {
        bodyService.saveWeight(memberId, request);
    }

    @Operation(summary = "신체 정보 입력하기")
    @PostMapping
    public void saveBodyInfo(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId,
        @Valid @RequestBody BodyInfoRequest request) {
        bodyService.saveBodyInfo(memberId, request);
    }

    @Operation(summary = "회원의 전체 눈바디 이미지 조회")
    @GetMapping("/images")
    public ApiSuccessResponse<Slice<BodyImageResponse>> findAllBodyImage(
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId, Pageable pageable) {
        return ApiSuccessResponse.from(bodyService.findAllBodyImage(memberId, pageable));
    }

    @Operation(summary = "해당하는 날짜의 눈바디 이미지 조회")
    @GetMapping("/image")
    public ApiSuccessResponse<BodyImageResponse> findTodayBodyImage(@RequestParam String dateTime,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(bodyService.findTodayBodyImage(memberId, dateTime));
    }

    @Operation(summary = "눈바디 이미지 업로드")
    @PostMapping("/image")
    public void saveBodyImage(@RequestParam String dateTime,
        @RequestPart(value = "files") List<MultipartFile> files,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        bodyService.saveBodyImage(files, dateTime, memberId);
    }

    @Operation(summary = "눈바디 이미지 삭제")
    @DeleteMapping("/image")
    public void deleteBodyImage(@RequestParam String url) {
        bodyService.deleteBodyImage(url);
    }

}
