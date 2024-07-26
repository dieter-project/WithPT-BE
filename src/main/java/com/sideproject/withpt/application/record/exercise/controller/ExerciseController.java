package com.sideproject.withpt.application.record.exercise.controller;

import com.sideproject.withpt.application.record.exercise.controller.request.ExerciseRequest;
import com.sideproject.withpt.application.record.exercise.controller.response.BookmarkCheckResponse;
import com.sideproject.withpt.application.record.exercise.controller.response.ExerciseInfoResponse;
import com.sideproject.withpt.application.record.exercise.controller.response.ExerciseResponse;
import com.sideproject.withpt.application.record.exercise.service.ExerciseService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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

    @Operation(summary = "북마크명과 중복되는 이름 있는지 체크")
    @GetMapping("/check")
    public ApiSuccessResponse<BookmarkCheckResponse> checkBookmark(@RequestParam String title,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(exerciseService.checkBookmark(title, memberId));
    }

    @Operation(summary = "운동 기록 입력")
    @PostMapping
    public void saveExercise(
        @RequestPart(value = "request") List<ExerciseRequest> request,
        @RequestPart(value = "files", required = false) List<MultipartFile> files,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        exerciseService.saveExercise(memberId, request, files);
    }

    @Operation(summary = "운동 기록 수정")
    @PatchMapping("/{exerciseId}")
    public void modifyExercise(@Valid @RequestBody ExerciseRequest request,
        @PathVariable Long exerciseId, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        exerciseService.modifyExercise(memberId, exerciseId, request);
    }

    @Operation(summary = "운동 기록 삭제")
    @DeleteMapping("/{exerciseId}")
    public void deleteExercise(@PathVariable Long exerciseId, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        exerciseService.deleteExercise(memberId, exerciseId);
    }

    @Operation(summary = "운동 이미지 삭제")
    @DeleteMapping("/image")
    public void deleteExerciseImage(@RequestParam String url) {
        exerciseService.deleteExerciseImage(url);
    }

}
