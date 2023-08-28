package com.sideproject.withpt.application.exercise.controller;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseCreateRequest;
import com.sideproject.withpt.application.exercise.dto.response.ExerciseListResponse;
import com.sideproject.withpt.application.exercise.exception.validator.ValidExerciseType;
import com.sideproject.withpt.application.exercise.service.ExerciseService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/exercise")
public class ExerciseController {

    private final ExerciseService exerciseService;

    // memberId 추출해서 사용할 수 있게 추후 수정
    // 운동 기록 조회 (
    @GetMapping
    public ApiSuccessResponse<List<ExerciseListResponse>> findAllExerciseList(Long memberId) {
        memberId = 1L;
        List<ExerciseListResponse> exerciseList = exerciseService.findAllExerciseList(memberId);
        return ApiSuccessResponse.from(exerciseList);
    }

    // 운동 기록 입력
    @PostMapping
    public ApiSuccessResponse saveExercise(@Valid @RequestBody ExerciseCreateRequest request, Long memberId) {
        memberId = 1L;
        exerciseService.saveExercise(memberId, request);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

    // 운동 기록 수정
    @PatchMapping("/{exerciseId}")
    public ApiSuccessResponse modifyExercise(@Valid @RequestBody ExerciseCreateRequest request,
                                             @PathVariable Long exerciseId, Long memberId) {
        memberId = 1L;
        exerciseService.modifyExercise(memberId, exerciseId, request);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

    // 운동 기록 삭제
    @DeleteMapping("/{exerciseId}")
    public ApiSuccessResponse deleteExercise(@PathVariable Long exerciseId, Long memberId) {
        memberId = 1L;
        exerciseService.deleteExercise(memberId, exerciseId);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

}
