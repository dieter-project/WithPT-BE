package com.sideproject.withpt.application.member.service;

import com.sideproject.withpt.application.member.dto.response.MemberSearchResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public Page<MemberSearchResponse> searchMembers(Pageable pageable, String name, String nickname) {
        return memberRepository.findBySearchOption(pageable, name, nickname);
    }
}
