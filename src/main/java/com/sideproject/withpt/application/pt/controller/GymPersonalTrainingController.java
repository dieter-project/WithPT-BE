package com.sideproject.withpt.application.pt.controller;

import com.sideproject.withpt.application.pt.controller.request.PersonalTrainingMemberRequest;
import com.sideproject.withpt.application.pt.controller.request.RemovePtMembersRequest;
import com.sideproject.withpt.application.pt.controller.response.GymsAndNumberOfMembersResponse;
import com.sideproject.withpt.application.pt.controller.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.pt.repository.dto.PtMemberListDto;
import com.sideproject.withpt.application.pt.service.PersonalTrainingService;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/v1/gyms")
public class GymPersonalTrainingController {

    private final PersonalTrainingService personalTrainingService;

    @Operation(summary = "체육관 회원 추가")
    @PostMapping("/{gymId}/personal-trainings/members")
    public ApiSuccessResponse<PersonalTrainingMemberResponse> registerPersonalTraining(@PathVariable Long gymId,
        @RequestBody PersonalTrainingMemberRequest request, @AuthenticationPrincipal Long trainerId) {

        return ApiSuccessResponse.from(
            personalTrainingService.registerPersonalTrainingMember(gymId, request.getMemberId(), trainerId)
        );
    }
    
    @Operation(summary = "체육관 회원 해제하기")
    @DeleteMapping("/{gymId}/personal-trainings/members")
    public void deletePtMembers(@PathVariable Long gymId, @AuthenticationPrincipal Long trainerId, @RequestBody RemovePtMembersRequest request) {
        personalTrainingService.deletePersonalTrainingMembers(gymId, trainerId, request.getMemberIds());
    }

    @Operation(summary = "특정 체육관 - 등록 요청 대기 중 회원 리스트 조회")
    @GetMapping("/{gymId}/personal-trainings/members/waiting")
    public ApiSuccessResponse<Page<PtMemberListDto>> listOfWaitingPtMembers(@PathVariable Long gymId, @AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            personalTrainingService.listOfPtMembersByRegistrationAllowedStatus(gymId, trainerId, PtRegistrationAllowedStatus.WAITING, pageable)
        );
    }

    @Operation(summary = "특정 체육관 - 승인된 회원 리스트 조회")
    @GetMapping("/{gymId}/personal-trainings/members/approved")
    public ApiSuccessResponse<Page<PtMemberListDto>> listOfApprovedPtMembers(@PathVariable Long gymId, @AuthenticationPrincipal Long trainerId, Pageable pageable) {
        return ApiSuccessResponse.from(
            personalTrainingService.listOfPtMembersByRegistrationAllowedStatus(gymId, trainerId, PtRegistrationAllowedStatus.APPROVED, pageable)
        );
    }

    @Operation(summary = "체육관 목록 및 PT 회원 수 조회", description = "체육관 목록과 각 회원 수 반환")
    @GetMapping("/personal-trainings")
    public ApiSuccessResponse<GymsAndNumberOfMembersResponse> listOfGymsAndNumberOfMembers(@AuthenticationPrincipal Long trainerId) {
        return ApiSuccessResponse.from(
            personalTrainingService.listOfGymsAndNumberOfMembers(trainerId)
        );
    }

    @Operation(summary = "특정 체육관 이름과 회원 수 조회")
    @GetMapping("/{gymId}")
    public ApiSuccessResponse<GymMemberCountDto> getGymAndNumberOfMembers(@AuthenticationPrincipal Long trainerId, @PathVariable Long gymId) {
        return ApiSuccessResponse.from(
            personalTrainingService.getGymAndNumberOfMembers(trainerId, gymId)
        );
    }


}
