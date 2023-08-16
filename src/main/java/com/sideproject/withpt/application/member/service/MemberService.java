package com.sideproject.withpt.application.member.service;

import com.sideproject.withpt.application.member.dto.request.MemberSignUpRequest;
import com.sideproject.withpt.application.member.dto.response.MemberSignUpResponse;
import com.sideproject.withpt.application.member.dto.response.NicknameCheckResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.Member;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public NicknameCheckResponse checkNickname(String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);

        //닉네임이 중복되는 경우
        if(optionalMember.isPresent()){
            throw GlobalException.DUPLICATE_NICKNAME;
        }

        // 사용 가능 시 : true;
        return NicknameCheckResponse.from(true);
    }

    @Transactional
    public MemberSignUpResponse signUpMember(MemberSignUpRequest params) {
        Optional<Member> optionalMember = memberRepository.findByEmail(params.getEmail());

        if(optionalMember.isPresent()){
            throw GlobalException.ALREADY_REGISTERED_MEMBER;
        }

        return MemberSignUpResponse.from(
            memberRepository.save(params.toEntity())
        );
    }
}
