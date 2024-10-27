package com.sideproject.withpt.application.lesson.controller;

import com.sideproject.withpt.application.lesson.controller.request.LessonChangeRequest;
import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.service.LessonManager;
import com.sideproject.withpt.application.lesson.service.LessonService;
import com.sideproject.withpt.application.lesson.service.response.AvailableLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.service.response.LessonInfoResponse;
import com.sideproject.withpt.application.lesson.service.response.LessonResponse;
import com.sideproject.withpt.application.lesson.service.response.LessonScheduleOfMonthResponse;
import com.sideproject.withpt.application.lesson.service.response.LessonScheduleResponse;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.common.type.LessonRequestStatus;
import com.sideproject.withpt.common.type.LessonStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LessonController {

    private final LessonService lessonService;
    private final LessonManager lessonManager;

    @Operation(summary = "신규 수업 등록")
    @PostMapping("/lessons/gyms/{gymId}")
    public ApiSuccessResponse<LessonResponse> registrationPtLesson(@PathVariable Long gymId,
        @Valid @RequestBody LessonRegistrationRequest request) {
        return ApiSuccessResponse.from(lessonManager.registrationPTLesson(gymId, request));
    }

    @Operation(summary = "[확정/취소] 수업 스케줄 정보 조회")
    @GetMapping("/lessons/{lessonId}")
    public ApiSuccessResponse<LessonInfoResponse> getLessonSchedule(@PathVariable Long lessonId) {
        return ApiSuccessResponse.from(
            lessonService.getLessonSchedule(lessonId)
        );
    }

    @Operation(summary = "수업 스케줄 변경")
    @PatchMapping("/lessons/{lessonId}")
    public ApiSuccessResponse<LessonResponse> changePtLesson(@PathVariable Long lessonId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
        @Valid @RequestBody LessonChangeRequest request) {

        return ApiSuccessResponse.from(
            lessonManager.changePTLesson(lessonId, userId, request)
        );
    }

    @Operation(summary = "수업관리/확정된 수업 - 수업 직접 취소하기")
    @PatchMapping("/lessons/{lessonId}/cancel")
    public ApiSuccessResponse<LessonResponse> cancelDecidedLesson(@PathVariable Long lessonId) {
        return ApiSuccessResponse.from(
            lessonService.cancelLesson(lessonId, LessonStatus.CANCELED)
        );
    }

    @Operation(summary = "취소 혹은 자동 취소된 수업 삭제하기")
    @DeleteMapping("/lessons/{lessonId}")
    public void deleteDecidedLesson(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
    }

    @Operation(summary = "예약 가능한 수업 시간 조회")
    @GetMapping("/lessons/available-times")
    public ApiSuccessResponse<AvailableLessonScheduleResponse> getTrainerWorkSchedule(
        @RequestParam Long gymId,
        @RequestParam Long trainerId,
        @RequestParam Day weekday,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiSuccessResponse.from(
            lessonService.getTrainerAvailableLessonSchedule(gymId, trainerId, weekday, date)
        );
    }

    @Operation(summary = "트레이너 - 날짜 별 수업 스케줄 조회")
    @GetMapping("/lessons/trainer-schedules")
    public ApiSuccessResponse<LessonScheduleResponse> getTrainerLessonScheduleByDate(
        @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId,
        @RequestParam(required = false, defaultValue = "-1") Long gymId,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiSuccessResponse.from(
            lessonService.getTrainerLessonScheduleByDate(trainerId, gymId, date)
        );
    }

    @Operation(summary = "회원 - 날짜 별 수업 스케줄 조회")
    @GetMapping("/lessons/member-schedules")
    public ApiSuccessResponse<LessonScheduleResponse> getLessonScheduleMembers(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiSuccessResponse.from(
            lessonService.getMemberLessonScheduleByDate(memberId, date)
        );
    }

    @Operation(summary = "트레이너 - 월(Month) 전체 체육관 수업 일정 달력 조회")
    @GetMapping("/lessons/trainer-schedules/monthly")
    public ApiSuccessResponse<LessonScheduleOfMonthResponse> getTrainerLessonScheduleOfMonth(
        @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId,
        @RequestParam(required = false, defaultValue = "-1") Long gymId,
        @RequestParam int year,
        @Valid @Min(1) @Max(12) @RequestParam int month
    ) {
        return ApiSuccessResponse.from(
            lessonService.getTrainerLessonScheduleOfMonth(trainerId, gymId, YearMonth.of(year, month))
        );
    }

    @Operation(summary = "회원 - 월(Month) 전체 체육관 수업 일정 달력 조회")
    @GetMapping("/lessons/member-schedules/monthly")
    public ApiSuccessResponse<LessonScheduleOfMonthResponse> getMemberLessonScheduleOfMonth(
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId,
        @RequestParam(required = false, defaultValue = "-1") Long gymId,
        @RequestParam int year,
        @Valid @Min(1) @Max(12) @RequestParam int month
    ) {
        return ApiSuccessResponse.from(
            lessonService.getMemberLessonScheduleOfMonth(memberId, gymId, YearMonth.of(year, month))
        );
    }


    @Operation(summary = "대기 수업 조회 - 내가 받은 요청")
    @GetMapping("/lessons/pending/received-requests")
    public ApiSuccessResponse<Map<LessonRequestStatus, Slice<LessonResponse>>> getReceivedLessonRequests(@Parameter(hidden = true) @AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            lessonService.getReceivedLessonRequests(trainerId, pageable)
        );
    }

    @Operation(summary = "대기 수업 조회 - 내가 보낸 요청")
    @GetMapping("/lessons/pending/sent-requests")
    public ApiSuccessResponse<Slice<LessonResponse>> getSentLessonRequests(@Parameter(hidden = true) @AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            lessonService.getSentLessonRequests(trainerId, pageable)
        );
    }

    @Operation(summary = "수업 등록 or 수업 스케줄 변경 수락하기")
    @PostMapping("/lessons/{lessonId}/accept")
    public ApiSuccessResponse<LessonResponse> lessonAccept(@PathVariable Long lessonId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        return ApiSuccessResponse.from(
            lessonManager.registrationOrScheduleChangeLessonAccept(userId, lessonId)
        );
    }
}
