package com.sideproject.withpt.application.gym.controller;

import com.sideproject.withpt.application.gym.service.GymService;
import com.sideproject.withpt.application.gym.service.response.GymResponse;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gyms")
public class GymController {

    private final GymService gymService;

    @Operation(summary = "체육관 리스트 조회")
    @GetMapping
    public ApiSuccessResponse<Slice<GymResponse>> listOfAllGyms(@Parameter(hidden = true) @AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            gymService.listOfAllGymsByPageable(trainerId, pageable)
        );
    }
}
