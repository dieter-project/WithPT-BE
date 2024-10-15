package com.sideproject.withpt.application.record.image.controller;

import com.sideproject.withpt.application.record.image.controller.request.DeleteImageRequest;
import com.sideproject.withpt.application.record.image.controller.request.ImageRequest;
import com.sideproject.withpt.application.record.image.service.ImageService;
import com.sideproject.withpt.application.record.image.service.response.ImageInfoResponse;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import com.sideproject.withpt.common.type.UsageType;
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
import org.springframework.http.MediaType;
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
@RequestMapping("/api/v1/members/record")
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "(운동, 눈바디) 이미지 조회")
    @GetMapping("/images")
    public ApiSuccessResponse<Slice<ImageInfoResponse>> findTodayBodyImage(
        @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) LocalDate uploadDate,
        @Valid @RequestParam UsageType type,
        Pageable pageable,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(
            imageService.findAllImage(memberId, uploadDate, type, pageable)
        );
    }

    @Operation(summary = "(운동, 눈바디) 이미지 삭제")
    @DeleteMapping("/images")
    public void deleteBodyImage(@RequestBody DeleteImageRequest request,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        imageService.deleteImage(memberId, request);
    }

    @Operation(summary = "눈바디 이미지 업로드")
    @PostMapping(value = "/images/body-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveBodyImage(
        @RequestPart(value = "files", required = false) List<MultipartFile> files,
        @RequestPart(value = "request") ImageRequest request,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        imageService.saveImage(files, request.getUploadDate(), memberId, UsageType.BODY);
    }

}
