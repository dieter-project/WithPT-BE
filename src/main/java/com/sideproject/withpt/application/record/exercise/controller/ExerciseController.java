package com.sideproject.withpt.application.record.exercise.controller;

import com.sideproject.withpt.application.record.RecordDelegator;
import com.sideproject.withpt.application.record.exercise.controller.request.ExerciseEditRequest;
import com.sideproject.withpt.application.record.exercise.controller.request.ExerciseRequest;
import com.sideproject.withpt.application.record.exercise.service.response.ExerciseInfoResponse;
import com.sideproject.withpt.application.record.exercise.service.response.ExerciseResponse;
import com.sideproject.withpt.application.record.exercise.service.ExerciseService;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/api/v1/members/record/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final RecordDelegator recordDelegator;

    @Operation(summary = "해당하는 날짜의 운동 기록 정보 조회")
    @GetMapping
    public ApiSuccessResponse<ExerciseResponse> findAllExerciseList(
        @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate uploadDate,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(
            exerciseService.findExerciseAndExerciseInfos(memberId, uploadDate)
        );
    }

    @Operation(summary = "운동 정보 단건 조회")
    @GetMapping("/{exerciseId}/exercise-info/{exerciseInfoId}")
    public ApiSuccessResponse<ExerciseInfoResponse> findOneExerciseInfo(
        @PathVariable Long exerciseId, @PathVariable Long exerciseInfoId) {
        return ApiSuccessResponse.from(
            exerciseService.findOneExerciseInfo(exerciseId, exerciseInfoId)
        );
    }

    @Operation(summary = "운동 기록 입력")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveExercise(
        @RequestPart(value = "request") List<ExerciseRequest> request,
        @RequestPart(value = "files", required = false) List<MultipartFile> files,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        if (request.size() == 0) {
            throw GlobalException.AT_LEAST_ONE_DATA_MUST_BE_INCLUDED;
        }
        LocalDate uploadDate = request.get(0).getUploadDate();
        recordDelegator.saveExerciseAndBookmark(memberId, request, files, uploadDate);
    }

    @Operation(summary = "운동 정보 수정")
    @PatchMapping("/{exerciseId}/exercise-info/{exerciseInfoId}")
    public void modifyExerciseInfo(@Valid @RequestBody ExerciseEditRequest request,
        @PathVariable Long exerciseId, @PathVariable Long exerciseInfoId) {
        exerciseService.modifyExercise(exerciseId, exerciseInfoId, request);
    }

    @Operation(summary = "운동 정보 삭제")
    @DeleteMapping("/{exerciseId}/exercise-info/{exerciseInfoId}")
    public void deleteExercise(@PathVariable Long exerciseId, @PathVariable Long exerciseInfoId) {
        exerciseService.deleteExerciseInfo(exerciseId, exerciseInfoId);
    }
}
