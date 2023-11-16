package com.sideproject.withpt.application.exercise.controller;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseRequest;
import com.sideproject.withpt.application.exercise.dto.response.BookmarkCheckResponse;
import com.sideproject.withpt.application.exercise.dto.response.ExerciseListResponse;
import com.sideproject.withpt.application.exercise.dto.response.ExerciseResponse;
import com.sideproject.withpt.application.exercise.service.ExerciseService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/exercise")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Operation(summary = "해당하는 날짜의 운동 기록 리스트 조회")
    @GetMapping
    public ApiSuccessResponse<ExerciseListResponse> findAllExerciseList(@RequestParam String dateTime, @AuthenticationPrincipal Long memberId) {
        ExerciseListResponse exerciseList = exerciseService.findAllExerciseList(memberId, dateTime);
        return ApiSuccessResponse.from(exerciseList);
    }

    @Operation(summary = "운동 기록 단건 조회")
    @GetMapping("/{exerciseId}")
    public ApiSuccessResponse<ExerciseResponse> findOneExercise(@PathVariable Long exerciseId, @AuthenticationPrincipal Long memberId) {
        ExerciseResponse exercise = exerciseService.findOneExercise(memberId, exerciseId);
        return ApiSuccessResponse.from(exercise);
    }

    @Operation(summary = "북마크명과 중복되는 이름 있는지 체크")
    @GetMapping("/check")
    public ApiSuccessResponse<BookmarkCheckResponse> checkBookmark(@RequestParam String title, @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(exerciseService.checkBookmark(title, memberId));
    }

    @Operation(summary = "운동 기록 입력")
    @PostMapping
    public void saveExercise(@RequestPart(value = "dto") List<ExerciseRequest> request,
                                           @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                           @AuthenticationPrincipal Long memberId) {
        exerciseService.saveExercise(memberId, request, files);
    }

    @Operation(summary = "운동 기록 수정")
    @PatchMapping("/{exerciseId}")
    public void modifyExercise(@Valid @RequestBody ExerciseRequest request,
                                             @PathVariable Long exerciseId, @AuthenticationPrincipal Long memberId) {
        exerciseService.modifyExercise(memberId, exerciseId, request);
    }

    @Operation(summary = "운동 기록 삭제")
    @DeleteMapping("/{exerciseId}")
    public void deleteExercise(@PathVariable Long exerciseId, @AuthenticationPrincipal Long memberId) {
        exerciseService.deleteExercise(memberId, exerciseId);
    }

    @Operation(summary = "운동 이미지 삭제")
    @DeleteMapping("/image")
    public void deleteExerciseImage(@RequestParam String url) {
        exerciseService.deleteExerciseImage(url);
    }

}
