package com.sideproject.withpt.application.lesson.controller;

import com.sideproject.withpt.application.lesson.controller.request.LessonChangeRequest;
import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.controller.response.AvailableLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.controller.response.LessonInfo;
import com.sideproject.withpt.application.lesson.controller.response.LessonMembersInGymResponse;
import com.sideproject.withpt.application.lesson.controller.response.LessonMembersResponse;
import com.sideproject.withpt.application.lesson.controller.response.SearchMemberResponse;
import com.sideproject.withpt.application.lesson.service.LessonLockFacade;
import com.sideproject.withpt.application.lesson.service.LessonService;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/v1/personal-trainings/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final LessonLockFacade lessonLockFacade;

    @Operation(summary = "수업관리/스케줄 - 수업등록")
    @PostMapping("/gym/{gymId}")
    public void registrationPtLesson(@PathVariable Long gymId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long loginId,
        @Valid @RequestBody LessonRegistrationRequest request) {

        String loginRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .findFirst().get().getAuthority().split("_")[1];

        log.info("로그인 role = {}", loginRole);


        lessonLockFacade.registrationPtLesson(gymId, loginId, loginRole, request);
    }

    @Operation(summary = "체육관 별 회원이름 검색")
    @GetMapping("/gym/{gymId}/members/search")
    public ApiSuccessResponse<Slice<SearchMemberResponse>> searchPtMemberInGym(
        @PathVariable Long gymId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId,
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

    @Operation(summary = "수업관리/메인 - 날짜 별 체육관 수업 스케줄 조회")
    @GetMapping
    public ApiSuccessResponse<LessonMembersResponse> getLessonScheduleMembers(
        @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId,
        @RequestParam(name = "gym", required = false) Long gymId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @RequestParam(required = false) LessonStatus status
    ) {
        log.info("체육관 {}, 날짜 {}, 상태 {}", gymId, date, status);

        return ApiSuccessResponse.from(
            lessonService.getLessonScheduleMembers(trainerId, gymId, date, status)
        );
    }

    @Operation(summary = "단일 수업 스케줄 조회")
    @GetMapping("/{lessonId}")
    public ApiSuccessResponse<LessonInfo> getLessonSchedule(@PathVariable Long lessonId) {
        return ApiSuccessResponse.from(
            lessonService.getLessonSchedule(lessonId)
        );
    }

    @Operation(summary = "수업관리/메인 - 해당 월(Month) 체육관 수업 일정 조회")
    @GetMapping("/days")
    public ApiSuccessResponse<List<LocalDate>> getLessonScheduleOfMonth(
        @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId,
        @RequestParam(name = "gym", required = false) Long gymId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth date
    ) {
        log.info("체육관 {}, 날짜 {}", gymId, date.toString());
        return ApiSuccessResponse.from(
            lessonService.getLessonScheduleOfMonth(trainerId, gymId, date)
        );
    }

    @Operation(summary = "수업관리/취소된 수업 > 삭제 알림 - 취소된 수업 삭제하기")
    @DeleteMapping("/{lessonId}")
    public void deleteDecidedLesson(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
    }

    @Operation(summary = "수업관리/확정된 수업 > 취소 알림 - 수업 취소하기")
    @PatchMapping("/{lessonId}/cancel")
    public void cancelDecidedLesson(@PathVariable Long lessonId) {
        lessonService.changeLessonStatus(lessonId, LessonStatus.CANCELED);
    }

    @Operation(summary = "수업관리/스케줄 - 수업변경")
    @PatchMapping("/{lessonId}")
    public void changePtLesson(@PathVariable Long lessonId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long loginId,
        @Valid @RequestBody LessonChangeRequest request) {

        String loginRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .findFirst().get().getAuthority().split("_")[1];

        log.info("로그인 role = {}", loginRole);

    }
}
