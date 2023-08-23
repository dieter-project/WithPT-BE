package com.sideproject.withpt.application.exercise.controller;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseCreateRequest;
import com.sideproject.withpt.application.exercise.dto.response.ExerciseListResponse;
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
@RequestMapping("/api/v1/exercise")
public class ExerciseController {

    private final ExerciseService exerciseService;

    // 운동 기록 조회
    // 일단 경로변수로 memberId 임시로 받아오게 하고 추후 수정 필요
    @GetMapping("/{memberId}")
    public ApiSuccessResponse<List<ExerciseListResponse>> findAllExerciseList(@PathVariable Long memberId) {
        return ApiSuccessResponse.from(exerciseService.findAllExerciseList(memberId));
    }

    // 운동 기록 입력
    @PostMapping
    public ApiSuccessResponse saveExercise(@Valid @RequestBody ExerciseCreateRequest request) {
        exerciseService.saveExercise(request);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

    // 운동 기록 수정
    @PatchMapping("/{exerciseId}")
    public ApiSuccessResponse modifyExercise(@Valid @RequestBody ExerciseCreateRequest request, @PathVariable Long exerciseId) {
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

    // 운동 기록 삭제
    @DeleteMapping("/{exerciseId}")
    public ApiSuccessResponse deleteExercise(@PathVariable Long exerciseId) {
        exerciseService.deleteExercise(exerciseId);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }


}
