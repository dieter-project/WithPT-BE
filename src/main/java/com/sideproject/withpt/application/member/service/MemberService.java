package com.sideproject.withpt.application.member.service;

import com.sideproject.withpt.application.member.dto.request.MemberSignUpRequest;
import com.sideproject.withpt.application.member.dto.response.NicknameCheckResponse;
import com.sideproject.withpt.application.member.exception.MemberException;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthTokenGenerator authTokenGenerator;

    public NicknameCheckResponse checkNickname(String nickname) {
        memberRepository.findByNickname(nickname).ifPresent(member -> {
            throw MemberException.DUPLICATE_NICKNAME;
        });

        // 사용 가능 시 : true
        return NicknameCheckResponse.from(true);
    }

    @Transactional
    public TokenSetDto signUpMember(MemberSignUpRequest params) {
        memberRepository.findByEmail(params.getEmail())
            .ifPresent(member -> {
                throw GlobalException.ALREADY_REGISTERED_USER;
            });

        Member savedMember = memberRepository.save(params.toMemberEntity());
        return authTokenGenerator.generateTokenSet(savedMember.getId(), Role.MEMBER);
    }
}
