package com.sideproject.withpt.application.pt.controller;

import com.sideproject.withpt.application.pt.controller.request.AcceptPtRegistrationRequest;
import com.sideproject.withpt.application.pt.controller.request.RemovePtMembersRequest;
import com.sideproject.withpt.application.pt.controller.response.CountOfMembersAndGymsResponse;
import com.sideproject.withpt.application.pt.controller.response.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.controller.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.pt.controller.response.TotalPtsCountResponse;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.pt.service.PersonalTrainingService;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personal-trainings")
public class GymPersonalTrainingController {

    private final PersonalTrainingService personalTrainingService;

    @Operation(summary = "체육관 회원 추가")
    @PostMapping("/gyms/{gymId}/members/{memberId}")
    public ApiSuccessResponse<PersonalTrainingMemberResponse> registerPersonalTraining(@PathVariable Long gymId,
        @PathVariable Long memberId, @AuthenticationPrincipal Long trainerId) {

        return ApiSuccessResponse.from(
            personalTrainingService.registerPersonalTrainingMember(gymId, memberId, trainerId)
        );
    }

    @Operation(summary = "체육관 회원 해제하기")
    @DeleteMapping("/gyms/{gymId}/members")
    public void deletePtMembers(@PathVariable Long gymId, @AuthenticationPrincipal Long trainerId,
        @RequestBody RemovePtMembersRequest request) {
        personalTrainingService.deletePersonalTrainingMembers(gymId, trainerId, request.getMemberIds());
    }

    @Operation(summary = "특정 체육관 - 등록 요청 대기 중 회원 리스트 조회")
    @GetMapping("/gyms/{gymId}/members/waiting")
    public ApiSuccessResponse<EachGymMemberListResponse> listOfWaitingPtMembers(@PathVariable Long gymId,
        @AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            personalTrainingService.listOfPtMembersByRegistrationAllowedStatus(gymId, trainerId,
                PtRegistrationAllowedStatus.WAITING, pageable)
        );
    }

    @Operation(summary = "알림 - PT 등록 승인")
    @PostMapping("/notification/registration-acceptance")
    public void allowPtRegistrationNotification (@RequestBody AcceptPtRegistrationRequest request) {
        personalTrainingService.allowPtRegistrationNotification(request);
    }

    @Operation(summary = "특정 체육관 - 승인된 회원 리스트 조회")
    @GetMapping("/gyms/{gymId}/members/approved")
    public ApiSuccessResponse<EachGymMemberListResponse> listOfApprovedPtMembers(@PathVariable Long gymId,
        @AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            personalTrainingService.listOfPtMembersByRegistrationAllowedStatus(gymId, trainerId,
                PtRegistrationAllowedStatus.APPROVED, pageable)
        );
    }

    @Operation(summary = "체육관 목록 및 PT 회원 수 조회", description = "체육관 목록과 각 회원 수 반환")
    @GetMapping("/gyms")
    public ApiSuccessResponse<Slice<CountOfMembersAndGymsResponse>> listOfGymsAndNumberOfMembers(
        @AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            personalTrainingService.listOfGymsAndNumberOfMembers(trainerId, pageable)
        );
    }

    @Operation(summary = "회원 관리 - 총 PT 회원 수 조회")
    @GetMapping("/gyms/members/count")
    public ApiSuccessResponse<TotalPtsCountResponse> countOfAllPtMembers(@AuthenticationPrincipal Long trainerId) {
        return ApiSuccessResponse.from(
            personalTrainingService.countOfAllPtMembers(trainerId)
        );
    }

    @Operation(summary = "특정 체육관 이름과 회원 수 조회")
    @GetMapping("/gyms/{gymId}")
    public ApiSuccessResponse<GymMemberCountDto> getGymAndNumberOfMembers(@AuthenticationPrincipal Long trainerId,
        @PathVariable Long gymId) {
        return ApiSuccessResponse.from(
            personalTrainingService.getGymAndNumberOfMembers(trainerId, gymId)
        );
    }


}
