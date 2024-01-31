package com.sideproject.withpt.application.gym.controller;

import com.sideproject.withpt.application.gym.service.GymQueryService;
import com.sideproject.withpt.application.lesson.controller.response.SearchMemberResponse;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import com.sideproject.withpt.domain.gym.Gym;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gyms")
public class GymController {

    private final GymQueryService gymQueryService;

    @Operation(summary = "체육관 리스트 조회")
    @GetMapping
    public ApiSuccessResponse<Slice<Gym>> listOfAllGyms(@AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            gymQueryService.listOfAllGymsByPageable(trainerId, pageable)
        );
    }

    @Operation(summary = "체육관에 등록된 회원이름 검색")
    @GetMapping("/{gymId}/members/search")
    public ApiSuccessResponse<Slice<SearchMemberResponse>> searchPtMemberInGym(
        @PathVariable Long gymId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId,
        @RequestParam(required = false) String name,
        Pageable pageable) {

        return ApiSuccessResponse.from(
            gymQueryService.searchMembersByGymIdAndName(gymId, trainerId, name, pageable)
        );
    }
}
