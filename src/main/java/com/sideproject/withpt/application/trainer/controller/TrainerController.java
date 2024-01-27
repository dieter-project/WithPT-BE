package com.sideproject.withpt.application.trainer.controller;

import com.sideproject.withpt.application.trainer.controller.request.InfoEditRequest;
import com.sideproject.withpt.application.trainer.controller.response.TrainerInfoResponse;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @Operation(summary = "트레이저 개인 정보 조회")
    @GetMapping("/{trainerId}/info")
    public ApiSuccessResponse<TrainerInfoResponse> getTrainerInfo(@PathVariable Long trainerId) {
        return ApiSuccessResponse.from(
            trainerService.getTrainerInfo(trainerId)
        );
    }

    @Operation(summary = "트레이저 개인 정보 수정")
    @PatchMapping(value = "/{trainerId}/info", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public void editTrainerInfo(@PathVariable Long trainerId,
        @Valid @RequestPart InfoEditRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file) {
        log.info("정보 수정 {}", request);

        trainerService.editTrainerInfo(trainerId, request, file);
    }
}
