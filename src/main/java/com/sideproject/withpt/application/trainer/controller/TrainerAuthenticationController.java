package com.sideproject.withpt.application.trainer.controller;

import com.sideproject.withpt.application.auth.service.response.AuthLoginResponse;
import com.sideproject.withpt.application.trainer.controller.request.TrainerSignUpRequest;
import com.sideproject.withpt.application.trainer.service.TrainerAuthenticationService;
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
public class TrainerAuthenticationController {

    private final TrainerAuthenticationService trainerAuthenticationService;

    @PostMapping("/sign-up")
    public ApiSuccessResponse<AuthLoginResponse> signUp(@Valid @RequestBody TrainerSignUpRequest request) {

        log.info("가입 정보 : {}", request);
        return ApiSuccessResponse.from(
            trainerAuthenticationService.signUp(request.toServiceTrainerSignUpDto())
        );
    }
}
