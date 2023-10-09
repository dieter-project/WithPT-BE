package com.sideproject.withpt.application.member.service;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
    }

    public List<Member> getAllMemberById(List<Long> memberIds) {
        return memberRepository.findAllByIdIn(memberIds);
    }
}
