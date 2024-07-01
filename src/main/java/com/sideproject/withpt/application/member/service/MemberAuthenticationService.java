package com.sideproject.withpt.application.member.service;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.MEMBER_REFRESH_TOKEN_PREFIX;

import com.sideproject.withpt.application.auth.controller.dto.OAuthLoginResponse;
import com.sideproject.withpt.application.member.controller.request.MemberSignUpRequest;
import com.sideproject.withpt.application.member.controller.response.NicknameCheckResponse;
import com.sideproject.withpt.application.member.exception.MemberException;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.application.type.Sex;
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

    public NicknameCheckResponse checkNickname(String nickname) {
        memberRepository.findByNickname(nickname).ifPresent(member -> {
            throw MemberException.DUPLICATE_NICKNAME;
        });

        // 사용 가능 시 : true
        return NicknameCheckResponse.from(true);
    }

    @Transactional
    public OAuthLoginResponse signUpMember(MemberSignUpRequest params) {
        memberRepository.findByEmail(params.getEmail())
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

        return OAuthLoginResponse.of(userId, params.getEmail(), params.getName(), params.getOauthProvider(),
            params.toMemberEntity()
                .getRole(), tokenSetDto);
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
