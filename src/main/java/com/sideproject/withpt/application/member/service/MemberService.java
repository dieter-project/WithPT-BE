package com.sideproject.withpt.application.member.service;

import com.sideproject.withpt.application.member.controller.request.EditMemberInfoRequest;
import com.sideproject.withpt.application.member.controller.response.MemberInfoResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.repository.dto.PtMemberListDto.MemberInfo;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Authentication;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.member.SocialLogin;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
    }

    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member findMember = getMemberById(memberId);
        return MemberInfoResponse.of(findMember, findMember.getAuthentication(), findMember.getSocialLogin());
    }

    @Transactional
    public void editMemberInfo(EditMemberInfoRequest request, Long memberId) {
        Member findMember = getMemberById(memberId);
        findMember.editMemberInfo(
            request.getName(),
            request.getBirth(),
            request.getSex(),
            request.getHeight(),
            request.getWeight()
        );
    }

    public List<Member> getAllMemberById(List<Long> memberIds) {
        return memberRepository.findAllByIdIn(memberIds);
    }
}
