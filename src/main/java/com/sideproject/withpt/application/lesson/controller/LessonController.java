package com.sideproject.withpt.application.lesson.controller;

import com.sideproject.withpt.application.lesson.controller.request.LessonChangeRequest;
import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.controller.response.AvailableLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.repository.dto.LessonInfoResponse;
import com.sideproject.withpt.application.lesson.controller.response.LessonMembersResponse;
import com.sideproject.withpt.application.lesson.controller.response.PendingLessonInfo;
import com.sideproject.withpt.application.lesson.service.LessonLockFacade;
import com.sideproject.withpt.application.lesson.service.LessonService;
import com.sideproject.withpt.application.lesson.service.response.LessonRegistrationResponse;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.LessonRequestStatus;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final LessonLockFacade lessonLockFacade;

    @Operation(summary = "신규 수업 등록")
    @PostMapping("/lessons/gyms/{gymId}")
    public ApiSuccessResponse<LessonRegistrationResponse> registrationPtLesson(@PathVariable Long gymId,
        @Valid @RequestBody LessonRegistrationRequest request) {

        String loginRole = getLoginRole();
        log.info("로그인 role = {}", loginRole);

        LessonRegistrationResponse response = lessonLockFacade.lessonConcurrencyCheck(() ->
                lessonService.registrationPTLesson(gymId, loginRole, request),
            lessonLockFacade.generateKey(request.getDate(), request.getTime())
        );
        return ApiSuccessResponse.from(response);
    }

    @Operation(summary = "확정/취소 수업 스케줄 조회")
    @GetMapping("/lessons/{lessonId}")
    public ApiSuccessResponse<LessonInfoResponse> getLessonSchedule(@PathVariable Long lessonId) {
        return ApiSuccessResponse.from(
            lessonService.getLessonSchedule(lessonId)
        );
    }

    @Operation(summary = "수업 스케줄 변경")
    @PatchMapping("/lessons/{lessonId}")
    public void changePtLesson(@PathVariable Long lessonId, @Valid @RequestBody LessonChangeRequest request) {

        String loginRole = getLoginRole();
        log.info("로그인 role = {}", loginRole);

        lessonLockFacade.lessonConcurrencyCheck(() ->
                lessonService.changePTLesson(lessonId, loginRole, request),
            lessonLockFacade.generateKey(request.getDate(), request.getTime())
        );
    }

    @Operation(summary = "예약 가능한 수업 시간 조회")
    @GetMapping("/gyms/{gymId}/trainers/{trainerId}/schedule")
    public ApiSuccessResponse<AvailableLessonScheduleResponse> getTrainerWorkSchedule(@PathVariable Long gymId,
        @PathVariable Long trainerId,
        @RequestParam Day weekday,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        return ApiSuccessResponse.from(
            lessonService.getTrainerWorkSchedule(gymId, trainerId, weekday, date)
        );
    }

    @Operation(summary = "수업관리/메인 - 대기 수업 조회")
    @GetMapping("/lessons/pending-lessons")
    public ApiSuccessResponse<Map<LessonRequestStatus, Map<LessonRequestStatus, List<PendingLessonInfo>>>> getPendingLessons(
        @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId
    ) {

        return ApiSuccessResponse.from(
            lessonService.getPendingLessons(trainerId)
        );
    }

    @Operation(summary = "수업관리/메인 - 날짜 별 체육관 수업 스케줄 조회")
    @GetMapping("/lessons")
    public ApiSuccessResponse<LessonMembersResponse> getLessonScheduleMembers(
        @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId,
        @RequestParam(name = "gym", required = false) Long gymId,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @RequestParam(required = false) LessonStatus status
    ) {
        log.info("체육관 {}, 날짜 {}, 상태 {}", gymId, date, status);

        return ApiSuccessResponse.from(
            lessonService.getLessonScheduleMembers(trainerId, gymId, date, status)
        );
    }

    @Operation(summary = "수업관리/메인 - 해당 월(Month) 체육관 수업 일정 달력 날짜 조회")
    @GetMapping("/lessons/days")
    public ApiSuccessResponse<List<LocalDate>> getLessonScheduleOfMonth(
        @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId,
        @RequestParam(name = "gym", required = false) Long gymId,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth date
    ) {
        log.info("체육관 {}, 날짜 {}", gymId, date.toString());
        return ApiSuccessResponse.from(
            lessonService.getLessonScheduleOfMonth(trainerId, gymId, date)
        );
    }

    @Operation(summary = "수업관리/취소된 수업 > 삭제 알림 - 취소된 수업 삭제하기")
    @DeleteMapping("/lessons/{lessonId}")
    public void deleteDecidedLesson(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
    }

    @Operation(summary = "수업관리/확정된 수업 > 취소 알림 - 수업 취소하기")
    @PatchMapping("/lessons/{lessonId}/cancel")
    public void cancelDecidedLesson(@PathVariable Long lessonId) {
        lessonService.changeLessonStatus(lessonId, LessonStatus.CANCELED);
    }

    private String getLoginRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(s -> s.contains("TRAINER") || s.contains("MEMBER"))
            .collect(Collectors.joining())
            .split("_")[1];
    }
}
