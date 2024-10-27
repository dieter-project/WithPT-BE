package com.sideproject.withpt.application.member.controller;

import com.sideproject.withpt.application.member.controller.request.EditMemberDietTypeRequest;
import com.sideproject.withpt.application.member.controller.request.EditMemberExerciseFrequencyRequest;
import com.sideproject.withpt.application.member.controller.request.EditMemberInfoRequest;
import com.sideproject.withpt.application.member.controller.request.EditMemberTargetWeightRequest;
import com.sideproject.withpt.application.member.service.response.MemberInfoResponse;
import com.sideproject.withpt.application.member.service.response.MemberSearchResponse;
import com.sideproject.withpt.application.member.service.MemberService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "이름으로 회원 검색")
    @GetMapping("/search")
    public ApiSuccessResponse<Slice<MemberSearchResponse>> searchMembers(Pageable pageable,
        @RequestParam(defaultValue = "WithPT") String name) {

        // TODO : 트레이너만 회원 검색이 가능하므로 security 부분 수정 or api를 이동
        return ApiSuccessResponse.from(
            memberService.searchMembers(pageable, name)
        );
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping("/info")
    public ApiSuccessResponse<MemberInfoResponse> getMemberInfo(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(
            memberService.getMemberInfo(memberId)
        );
    }

    @Operation(summary = "내 정보 수정")
    @PatchMapping("/info")
    public void editMemberInfo(@Valid @RequestBody EditMemberInfoRequest request,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        memberService.editMemberInfo(request, memberId);
    }

    @Operation(summary = "식단 수정")
    @PatchMapping("/info/diet")
    public void editMemberDietType(@Valid @RequestBody EditMemberDietTypeRequest request, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        memberService.editDietType(request, memberId);
    }

    @Operation(summary = "운동목표 수정")
    @PatchMapping("/info/exercise")
    public void editMemberExerciseFrequency(@Valid @RequestBody EditMemberExerciseFrequencyRequest request, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        memberService.editExerciseFrequency(request, memberId);
    }

    @Operation(summary = "목표체중 수정")
    @PatchMapping("/info/weight")
    public void editMemberTargetWeight(@Valid @RequestBody EditMemberTargetWeightRequest request, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        memberService.editTargetWeight(request, memberId);
    }

}