package com.sideproject.withpt.application.member.controller;

import com.sideproject.withpt.application.member.dto.request.MemberSignUpRequest;
import com.sideproject.withpt.application.member.dto.response.NicknameCheckResponse;
import com.sideproject.withpt.application.member.service.MemberService;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/sign-up")
    public ApiSuccessResponse<TokenSetDto> signUp(@Valid @RequestBody MemberSignUpRequest request) {
        return ApiSuccessResponse.from(memberService.signUpMember(request));
    }

    @GetMapping("/nickname/check")
    public ApiSuccessResponse<NicknameCheckResponse> nicknameCheck(@RequestParam String nickname) {
        return ApiSuccessResponse.from(memberService.checkNickname(nickname));
    }
}
