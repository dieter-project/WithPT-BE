package com.sideproject.withpt.application.exercise.controller;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseRequest;
import com.sideproject.withpt.application.exercise.dto.response.ExerciseListResponse;
import com.sideproject.withpt.application.exercise.service.ExerciseService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
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

    // 운동 기록 리스트 조회
    @GetMapping
    public ApiSuccessResponse<List<ExerciseListResponse>> findAllExerciseList(@RequestParam String dateTime, @AuthenticationPrincipal Long memberId) {
        List<ExerciseListResponse> exerciseList = exerciseService.findAllExerciseList(memberId, dateTime);
        return ApiSuccessResponse.from(exerciseList);
    }

    // 해당 운동 기록 조회
    @GetMapping("/{exerciseId}")
    public ApiSuccessResponse<ExerciseListResponse> findOneExercise(@PathVariable Long exerciseId, @AuthenticationPrincipal Long memberId) {
        ExerciseListResponse exercise = exerciseService.findOneExercise(memberId, exerciseId);
        return ApiSuccessResponse.from(exercise);
    }

    // 운동 기록 입력
    @PostMapping
    public ApiSuccessResponse saveExercise(@RequestPart(value = "dto") List<ExerciseRequest> request,
                                           @RequestPart(value = "files") List<MultipartFile> files,
                                           @AuthenticationPrincipal Long memberId) {
        exerciseService.saveExercise(memberId, request, files);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

    // 운동 기록 수정
    @PatchMapping("/{exerciseId}")
    public ApiSuccessResponse modifyExercise(@Valid @RequestBody ExerciseRequest request,
                                             @PathVariable Long exerciseId, @AuthenticationPrincipal Long memberId) {
        exerciseService.modifyExercise(memberId, exerciseId, request);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

    // 운동 기록 삭제
    @DeleteMapping("/{exerciseId}")
    public ApiSuccessResponse deleteExercise(@PathVariable Long exerciseId, @AuthenticationPrincipal Long memberId) {
        exerciseService.deleteExercise(memberId, exerciseId);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

}
