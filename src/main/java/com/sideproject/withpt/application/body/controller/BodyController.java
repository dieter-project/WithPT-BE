package com.sideproject.withpt.application.body.controller;

import com.sideproject.withpt.application.body.dto.request.BodyInfoRequest;
import com.sideproject.withpt.application.body.dto.request.WeightInfoRequest;
import com.sideproject.withpt.application.body.dto.response.WeightInfoResponse;
import com.sideproject.withpt.application.body.service.BodyService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/body")
public class BodyController {

    private final BodyService bodyService;

    // 해당 날짜의 체중 및 신체 정보 조회하기
    @GetMapping
    public ApiSuccessResponse<WeightInfoResponse> findWeight(@AuthenticationPrincipal Long memberId, @RequestBody LocalDateTime dateTime) {
        WeightInfoResponse weightInfo = bodyService.findWeightInfo(memberId, dateTime);
        return ApiSuccessResponse.from(weightInfo);
    }

    // 체중 입력하기
    @PostMapping("/weight")
    public ApiSuccessResponse saveWeight(@AuthenticationPrincipal Long memberId, @Valid @RequestBody WeightInfoRequest request) {
        bodyService.saveWeight(memberId, request);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

    // 신체 정보 입력하기
    @PostMapping
    public ApiSuccessResponse saveBodyInfo(@AuthenticationPrincipal Long memberId, @Valid @RequestBody BodyInfoRequest request) {
        bodyService.saveBodyInfo(memberId, request);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

}
