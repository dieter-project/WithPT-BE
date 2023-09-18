package com.sideproject.withpt.application.trainer.controller;

import com.sideproject.withpt.application.trainer.controller.request.TrainerSignUpRequest;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping("/sign-up")
    public ApiSuccessResponse<TokenSetDto> signUp(@Valid @RequestBody TrainerSignUpRequest request) {
        return ApiSuccessResponse.from(
            trainerService.signUp(request.toServiceTrainerSignUpDto())
        );
    }
}
