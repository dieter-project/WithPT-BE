package com.sideproject.withpt.application.schedule.controller;

import com.sideproject.withpt.application.schedule.controller.request.WorkScheduleEditRequest;
import com.sideproject.withpt.application.schedule.service.response.WorkScheduleResponse;
import com.sideproject.withpt.application.schedule.service.WorkScheduleService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    @Operation(summary = "트레이너 특정 체육관 근무 스케줄 조회")
    @GetMapping("/gyms/{gymId}/trainers/{trainerId}/schedules")
    public ApiSuccessResponse<List<WorkScheduleResponse>> getAllWorkSchedule(@PathVariable Long trainerId, @PathVariable Long gymId) {
        return ApiSuccessResponse.from(
            workScheduleService.getAllWorkScheduleByGym(gymId, trainerId)
        );
    }

    @Operation(summary = "트레이너 근무 스케줄 단건 조회")
    @GetMapping("/gyms/{gymId}/trainers/{trainerId}/schedules/{scheduleId}")
    public ApiSuccessResponse<WorkScheduleResponse> getWorkSchedule(@PathVariable Long scheduleId) {
        return ApiSuccessResponse.from(
            workScheduleService.getWorkSchedule(scheduleId)
        );
    }

    @Operation(summary = "트레이너 근무 스케줄 수정")
    @PatchMapping("/gyms/{gymId}/trainers/{trainerId}/schedules")
    public void editWorkSchedule(@PathVariable Long gymId, @PathVariable Long trainerId,
        @Valid @RequestBody WorkScheduleEditRequest request) {
        workScheduleService.editWorkSchedule(trainerId, gymId, request);
    }

}
