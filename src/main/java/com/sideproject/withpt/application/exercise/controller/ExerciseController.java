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
    @GetMapping("/{memberId}")
    public ApiSuccessResponse<List<ExerciseListResponse>> findAllExerciseList(@PathVariable Long memberId) {
        return ApiSuccessResponse.from(exerciseService.findAllExerciseList(memberId));
    }

    // 운동 기록 입력
    @PostMapping
    public ApiSuccessResponse<Void> saveExercise(@Valid @RequestBody ExerciseCreateRequest request) {
        exerciseService.saveExercise(request);
        return ApiSuccessResponse.from(null);
    }

}
