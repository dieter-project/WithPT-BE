package com.sideproject.withpt.application.member.controller;

import com.sideproject.withpt.application.member.dto.response.MemberSearchResponse;
import com.sideproject.withpt.application.member.service.MemberQueryService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/search")
    public ApiSuccessResponse<Page<MemberSearchResponse>> searchMembers(Pageable pageable,
        @RequestParam(defaultValue = "WithPT") String name,
        @RequestParam(required = false) String nickname) {

        return ApiSuccessResponse.from(
            memberQueryService.searchMembers(pageable, name, nickname)
        );
    }
}
