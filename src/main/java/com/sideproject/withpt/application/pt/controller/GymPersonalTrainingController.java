package com.sideproject.withpt.application.pt.controller;

import com.sideproject.withpt.application.pt.controller.request.AcceptPtRegistrationRequest;
import com.sideproject.withpt.application.pt.controller.request.ExtendPtRequest;
import com.sideproject.withpt.application.pt.controller.request.RemovePtMembersRequest;
import com.sideproject.withpt.application.pt.controller.request.SavePtMemberDetailInfoRequest;
import com.sideproject.withpt.application.pt.controller.request.UpdatePtMemberDetailInfoRequest;
import com.sideproject.withpt.application.pt.controller.response.CountOfMembersAndGymsResponse;
import com.sideproject.withpt.application.pt.controller.response.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.controller.response.MemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.pt.controller.response.ReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.controller.response.TotalAndRemainingPtCountResponse;
import com.sideproject.withpt.application.pt.controller.response.TotalPtsCountResponse;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.pt.service.PersonalTrainingService;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @PostMapping("/{gymId}/personal-trainings/members/{memberId}/info")
    public void savePtMemberDetailInfo(
        @PathVariable Long memberId, @PathVariable Long gymId, @RequestBody SavePtMemberDetailInfoRequest request, @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId
    ) {
        personalTrainingService.savePtMemberDetailInfo(memberId, trainerId, gymId, request);
    }

    @Operation(summary = "회원 PT 잔여 및 전체 횟수 조회")
    @GetMapping("/{gymId}/personal-trainings/members/{memberId}/info/pt-count")
    public ApiSuccessResponse<TotalAndRemainingPtCountResponse> getPtTotalAndRemainingCount(@PathVariable Long memberId, @PathVariable Long gymId, @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId) {
        return ApiSuccessResponse.from(
            personalTrainingService.getPtTotalAndRemainingCount(memberId, trainerId, gymId)
        );
    }

    @Operation(summary = "PT 회원 세부 정보 수정")
    @PatchMapping("/{gymId}/personal-trainings/members/{memberId}/info")
    public void updatePtMemberDetailInfo(@PathVariable Long memberId, @PathVariable Long gymId, @RequestBody UpdatePtMemberDetailInfoRequest request, @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId) {
        personalTrainingService.updatePtMemberDetailInfo(memberId, trainerId, gymId, request);
    }

    @Operation(summary = "PT 횟수 연장하기")
    @PatchMapping("/{gymId}/personal-trainings/members/{memberId}")
    public void extendPt(@PathVariable Long memberId, @PathVariable Long gymId, @RequestBody ExtendPtRequest request, @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId) {
        personalTrainingService.extendPt(memberId, trainerId, gymId, request);
    }

    @Operation(summary = "PT 재등록 히스토리")
    @GetMapping("/{gymId}/personal-trainings/members/{memberId}/history")
    public ApiSuccessResponse<Slice<ReRegistrationHistoryResponse>> getReRegistrationHistory(@PathVariable Long memberId, @PathVariable Long gymId, @Parameter(hidden = true) @AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            personalTrainingService.getReRegistrationHistory(memberId, trainerId, gymId, pageable)
        );
    }
}
