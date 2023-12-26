package com.sideproject.withpt.application.lesson.controller;

import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.controller.response.AvailableLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.controller.response.SearchMemberResponse;
import com.sideproject.withpt.application.lesson.service.LessonService;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personal-trainings/lessons")
public class LessonController {

    private final LessonService lessonService;

    @Operation(summary = "수업관리/스케줄 - 수업등록")
    @PostMapping("/gym/{gymId}")
    public void registrationPtLesson(@PathVariable Long gymId,
        @AuthenticationPrincipal Long loginId,
        @Valid @RequestBody LessonRegistrationRequest request) {

        String loginRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .findFirst().get().getAuthority();

        lessonService.registrationPtLesson(gymId, loginId, loginRole, request);
    }

    @Operation(summary = "수업관리/스케줄 - 수업등록 => 회원이름 검색")
    @GetMapping("/gym/{gymId}/members")
    public ApiSuccessResponse<Slice<SearchMemberResponse>> searchPtMemberInGym(
        @PathVariable Long gymId, @AuthenticationPrincipal Long trainerId,
        @RequestParam(required = false) String name,
        Pageable pageable) {

        return ApiSuccessResponse.from(
            lessonService.searchMembersByGymIdAndName(gymId, trainerId, name, pageable)
        );
    }

    @Operation(summary = "수업관리/스케줄 - 수업등록 => 트레이너 근무 시간 조회")
    @GetMapping("/gym/{gymId}/trainers/{trainerId}/schedule")
    public ApiSuccessResponse<AvailableLessonScheduleResponse> getTrainerWorkSchedule(@PathVariable Long gymId,
        @PathVariable Long trainerId,
        @RequestParam Day weekday,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        return ApiSuccessResponse.from(
            lessonService.getTrainerWorkSchedule(gymId, trainerId, weekday, date)
        );
    }
}
