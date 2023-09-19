package com.sideproject.withpt.application.gym.controller;

import com.sideproject.withpt.application.gym.controller.response.TrainerAllGymsResponse;
import com.sideproject.withpt.application.gym.service.GymQueryService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gyms")
public class GymController {

    private final GymQueryService gymQueryService;

    @GetMapping
    public ApiSuccessResponse<TrainerAllGymsResponse> listOfAllGyms(@AuthenticationPrincipal Long trainerId) {
        return ApiSuccessResponse.from(
            gymQueryService.listOfTrainerAllGyms(trainerId)
        );
    }
}
