package com.sideproject.withpt.application.record.image.controller;

import com.sideproject.withpt.application.record.image.controller.request.DeleteImageRequest;
import com.sideproject.withpt.application.record.image.service.ImageService;
import com.sideproject.withpt.application.record.image.service.response.ImageInfoResponse;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import com.sideproject.withpt.common.type.Usages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/record/exercises")
public class ExerciseImageController {

    private final ImageService imageService;

    @Operation(summary = "회원의 전체 운동 이미지 조회")
    @GetMapping("/images")
    public ApiSuccessResponse<Slice<ImageInfoResponse>> findAllBodyImage(
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId, Pageable pageable) {
        return ApiSuccessResponse.from(
            imageService.findAllImage(memberId, null, Usages.EXERCISE, pageable)
        );
    }

    @Operation(summary = "해당하는 날짜의 운동 이미지 조회")
    @GetMapping("/image")
    public ApiSuccessResponse<Slice<ImageInfoResponse>> findTodayBodyImage(
        @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate uploadDate, Pageable pageable,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(
            imageService.findAllImage(memberId, uploadDate, Usages.EXERCISE, pageable)
        );
    }

    @Operation(summary = "운동 이미지 삭제")
    @DeleteMapping("/image")
    public void deleteBodyImage(@RequestBody DeleteImageRequest request,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        imageService.deleteImage(memberId, request);
    }

}
