package com.sideproject.withpt.application.member.service;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.MEMBER_REFRESH_TOKEN_PREFIX;

import com.sideproject.withpt.application.auth.service.dto.AuthLoginResponse;
import com.sideproject.withpt.application.member.controller.request.MemberSignUpRequest;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.domain.member.Member;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAuthenticationService {

    private final MemberRepository memberRepository;
    private final AuthTokenGenerator authTokenGenerator;
    private final RedisClient redisClient;

    @Transactional
    public AuthLoginResponse signUpMember(MemberSignUpRequest params) {
        memberRepository.findByEmailAndAuthProvider(params.getEmail(), params.getAuthProvider())
            .ifPresent(member -> {
                throw GlobalException.ALREADY_REGISTERED_USER;
            });

        Member member = params.toMemberEntity();
        member.addDefaultImageUrl(getDefaultProfileImageBySex(params.getSex()));

        Long userId = memberRepository.save(member).getId();

        TokenSetDto tokenSetDto = authTokenGenerator.generateTokenSet(userId, Role.MEMBER);
        redisClient.put(
            MEMBER_REFRESH_TOKEN_PREFIX + userId,
            tokenSetDto.getRefreshToken(),
            TimeUnit.SECONDS,
            tokenSetDto.getRefreshExpiredAt()
        );

        return AuthLoginResponse.of(userId, params.getEmail(), params.getName(), params.getAuthProvider(),
            params.toMemberEntity().getRole(), tokenSetDto);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        memberRepository.delete(member);
    }

    private String getDefaultProfileImageBySex(Sex sex) {
        return sex.equals(Sex.MAN) ?
            "https://withpt-s3.s3.ap-northeast-2.amazonaws.com/PROFILE/default_profile/MEMBER_MAN.png" :
            "https://withpt-s3.s3.ap-northeast-2.amazonaws.com/PROFILE/default_profile/MEMBER_WOMAN.png";
    }
}
