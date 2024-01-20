package com.sideproject.withpt.application.pt.controller;

import static com.sideproject.withpt.application.pt.exception.PtConstants.MAX_QUERY_MONTHS;

import com.sideproject.withpt.application.pt.controller.request.ExtendPtRequest;
import com.sideproject.withpt.application.pt.controller.request.RemovePtMembersRequest;
import com.sideproject.withpt.application.pt.controller.request.SavePtMemberDetailInfoRequest;
import com.sideproject.withpt.application.pt.controller.request.UpdatePtMemberDetailInfoRequest;
import com.sideproject.withpt.application.pt.controller.response.AssignedPTInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.CountOfMembersAndGymsResponse;
import com.sideproject.withpt.application.pt.controller.response.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.controller.response.MemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.pt.controller.response.PtStatisticResponse;
import com.sideproject.withpt.application.pt.controller.response.ReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.controller.response.TotalAndRemainingPtCountResponse;
import com.sideproject.withpt.application.pt.controller.response.TotalPtsCountResponse;
import com.sideproject.withpt.application.pt.exception.PTErrorCode;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.pt.service.PersonalTrainingService;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
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
@RequestMapping("/api/v1/gyms")
public class GymPersonalTrainingController {

    private final PersonalTrainingService personalTrainingService;

    @Operation(summary = "체육관 회원 추가")
    @PostMapping("/{gymId}/personal-trainings/members/{memberId}")
    public ApiSuccessResponse<PersonalTrainingMemberResponse> registerPersonalTraining(@PathVariable Long gymId,
        @PathVariable Long memberId, @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId) {

        return ApiSuccessResponse.from(
            personalTrainingService.registerPersonalTrainingMember(gymId, memberId, trainerId)
        );
    }

    @Operation(summary = "체육관 회원 해제하기")
    @DeleteMapping("/personal-trainings/members")
    public void deletePtMembers(@RequestBody RemovePtMembersRequest request) {
        personalTrainingService.deletePersonalTrainingMembers(request.getPtIds());
    }

    @Operation(summary = "특정 체육관 - 등록 요청 대기 중 회원 리스트 조회")
    @GetMapping("/{gymId}/personal-trainings/members/waiting")
    public ApiSuccessResponse<EachGymMemberListResponse> listOfWaitingPtMembers(@PathVariable Long gymId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            personalTrainingService.listOfPtMembersByRegistrationAllowedStatus(gymId, trainerId,
                PtRegistrationAllowedStatus.WAITING, pageable)
        );
    }

    @Operation(summary = "알림 - 회원이 PT 등록 승인")
    @PatchMapping("/personal-trainings/{ptId}/members/registration-acceptance")
    public void allowPtRegistrationNotification (@PathVariable Long ptId) {
        personalTrainingService.allowPtRegistrationNotification(ptId);
    }

    @Operation(summary = "특정 체육관 - 승인된 회원 리스트 조회")
    @GetMapping("/{gymId}/personal-trainings/members/approved")
    public ApiSuccessResponse<EachGymMemberListResponse> listOfApprovedPtMembers(@PathVariable Long gymId,
        @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            personalTrainingService.listOfPtMembersByRegistrationAllowedStatus(gymId, trainerId,
                PtRegistrationAllowedStatus.APPROVED, pageable)
        );
    }

    @Operation(summary = "체육관 목록 및 PT 회원 수 조회", description = "체육관 목록과 각 회원 수 반환")
    @GetMapping("/personal-trainings")
    public ApiSuccessResponse<Slice<CountOfMembersAndGymsResponse>> listOfGymsAndNumberOfMembers(
        @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            personalTrainingService.listOfGymsAndNumberOfMembers(trainerId, pageable)
        );
    }

    @Operation(summary = "회원 관리 - 총 PT 회원 수 조회")
    @GetMapping("/personal-trainings/members/count")
    public ApiSuccessResponse<TotalPtsCountResponse> countOfAllPtMembers(@Parameter(hidden = true) @AuthenticationPrincipal Long trainerId) {
        return ApiSuccessResponse.from(
            personalTrainingService.countOfAllPtMembers(trainerId)
        );
    }

    @Operation(summary = "특정 체육관 이름과 회원 수 조회")
    @GetMapping("/{gymId}/personal-trainings")
    public ApiSuccessResponse<GymMemberCountDto> getGymAndNumberOfMembers(@Parameter(hidden = true) @AuthenticationPrincipal Long trainerId,
        @PathVariable Long gymId) {
        return ApiSuccessResponse.from(
            personalTrainingService.getGymAndNumberOfMembers(trainerId, gymId)
        );
    }

    @Operation(summary = "세부 정보 입력 필요 상태일 때 - 회원 정보 조회")
    @GetMapping("/personal-trainings/{ptId}/member/info")
    public ApiSuccessResponse<MemberDetailInfoResponse> getPtMemberDetailInfo(@PathVariable Long ptId) {
        return ApiSuccessResponse.from(
            personalTrainingService.getPtMemberDetailInfo(ptId)
        );
    }

    @Operation(summary = "PT 회원 세부 정보 초기 입력")
    @PostMapping("/personal-trainings/{ptId}/member/info")
    public void savePtMemberDetailInfo(@PathVariable Long ptId, @Valid @RequestBody SavePtMemberDetailInfoRequest request) {
        personalTrainingService.savePtMemberDetailInfo(ptId, request);
    }

    @Operation(summary = "회원 PT 잔여 및 전체 횟수 조회")
    @GetMapping("/personal-trainings/{ptId}/member/info/pt-count")
    public ApiSuccessResponse<TotalAndRemainingPtCountResponse> getPtTotalAndRemainingCount(@PathVariable Long ptId) {
        return ApiSuccessResponse.from(
            personalTrainingService.getPtTotalAndRemainingCount(ptId)
        );
    }

    @Operation(summary = "PT 회원 세부 정보 수정")
    @PatchMapping("/personal-trainings/{ptId}/member/info")
    public void updatePtMemberDetailInfo(@PathVariable Long ptId, @RequestBody UpdatePtMemberDetailInfoRequest request) {
        personalTrainingService.updatePtMemberDetailInfo(ptId, request);
    }

    @Operation(summary = "PT 횟수 연장하기")
    @PatchMapping("/personal-trainings/{ptId}")
    public void extendPt(@PathVariable Long ptId, @Valid @RequestBody ExtendPtRequest request) {
        personalTrainingService.extendPt(ptId, request);
    }

    @Operation(summary = "PT 재등록 히스토리")
    @GetMapping("/personal-trainings/{ptId}/member/info/history")
    public ApiSuccessResponse<Slice<ReRegistrationHistoryResponse>> getReRegistrationHistory(@PathVariable Long ptId, Pageable pageable) {
        return ApiSuccessResponse.from(
            personalTrainingService.getReRegistrationHistory(ptId, pageable)
        );
    }

    @Operation(summary = "담당 트레이너 정보 조회")
    @GetMapping("/personal-trainings/members/{memberId}/trainers")
    public ApiSuccessResponse<List<AssignedPTInfoResponse>> getPtAssignedTrainerInformation(@PathVariable Long memberId) {
        return ApiSuccessResponse.from(
            personalTrainingService.getPtAssignedTrainerInformation(memberId)
        );
    }

    @Operation(summary = "트레이너의 모든 담당 회원 정보 조회")
    @GetMapping("/personal-trainings/trainers/{trainerId}/members")
    public ApiSuccessResponse<List<MemberDetailInfoResponse>> getPtAssignedMemberInformation(@PathVariable Long trainerId) {
        return ApiSuccessResponse.from(
            personalTrainingService.getPtAssignedMemberInformation(trainerId)
        );
    }

    @Operation(summary = "회원 통계 정보 조회")
    @GetMapping("/personal-trainings/statistics")
    public ApiSuccessResponse<PtStatisticResponse> getPtStatistics(@Parameter(hidden = true) @AuthenticationPrincipal Long trainerId,
        @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate date,
        @RequestParam(defaultValue = "12") int size) {

        if(size > MAX_QUERY_MONTHS) {
            throw new PTException(PTErrorCode.MAX_QUERY_MONTHS);
        }

        return ApiSuccessResponse.from(
            personalTrainingService.getPtStatistics(trainerId, date, size)
        );
    }
}
