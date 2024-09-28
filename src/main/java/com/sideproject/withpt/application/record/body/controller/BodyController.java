package com.sideproject.withpt.application.record.body.controller;

import com.sideproject.withpt.application.record.body.controller.request.BodyInfoRequest;
import com.sideproject.withpt.application.record.body.controller.request.WeightInfoRequest;
import com.sideproject.withpt.application.record.body.controller.response.WeightInfoResponse;
import com.sideproject.withpt.application.record.body.service.BodyService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        bodyService.saveOrUpdateWeight(memberId, request);
    }

    @Operation(summary = "신체 정보 입력하기")
    @PostMapping("/bodyInfo")
    public void saveBodyInfo(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId,
        @Valid @RequestBody BodyInfoRequest request) {
        bodyService.saveOrUpdateBodyInfo(memberId, request);
    }


}
