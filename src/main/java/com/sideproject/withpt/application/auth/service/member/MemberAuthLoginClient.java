package com.sideproject.withpt.application.auth.service.member;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.MEMBER_REFRESH_TOKEN_PREFIX;

import com.sideproject.withpt.application.auth.infra.AuthLoginParams;
import com.sideproject.withpt.application.auth.infra.OAuthInfoResponse;
import com.sideproject.withpt.application.auth.service.AuthLoginClient;
import com.sideproject.withpt.application.auth.service.RequestOAuthInfoService;
import com.sideproject.withpt.application.auth.service.dto.AuthLoginResponse;
import com.sideproject.withpt.application.auth.service.dto.LoginResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.member.Member;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberAuthLoginClient implements AuthLoginClient {

    private final AuthTokenGenerator authTokenGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final RedisClient redisClient;

    private final MemberRepository memberRepository;
    private final PersonalTrainingRepository personalTrainingRepository;

    @Override
    public Role role() {
        return Role.MEMBER;
    }

    @Override
    public LoginResponse login(AuthLoginParams params) {
        if (params.authProvider() == AuthProvider.EMAIL) {
            return passwordLogin(params);
        }

        // 소셜 로그인 처리
        OAuthInfoResponse oAuthInfoResponse = getOAuthInfo(params);

        // 회원 존재 여부 확인 - 이미 가입된 회원 토큰 발급
        if (memberRepository.existsByEmail(oAuthInfoResponse.getEmail())) { // true : 이미 존재하면
            return existinglogin(oAuthInfoResponse.getEmail(), oAuthInfoResponse.getOAuthProvider(), params.registerRole());
        }

        // 신규 회원이면 email, provider, role 반환
        return LoginResponse.of(
            AuthLoginResponse.of(oAuthInfoResponse, params.registerRole())
        );
    }

    private LoginResponse passwordLogin(AuthLoginParams params) {
        String email = params.email();
        String password = params.password();

        Member member = memberRepository.findByEmailAndAuthProvider(email, params.authProvider())
            .orElseThrow(() -> GlobalException.CREDENTIALS_DO_NOT_EXIST);

        if (!member.getPassword().equals(password)) {
            throw GlobalException.INVALID_PASSWORD;
        }

        return LoginResponse.of(
            getAuthLoginResponse(member.getRole(), member),
            personalTrainingRepository.findPtAssignedTrainerInformation(member)
        );
    }

    // 소셜 정보 획득
    private OAuthInfoResponse getOAuthInfo(AuthLoginParams params) {
        return requestOAuthInfoService.request(params);
    }

    // 이미 가입된 회원 : 토큰 발급
    private LoginResponse existinglogin(String email, AuthProvider authProvider, Role role) {
        Member member = memberRepository.findByEmailAndAuthProvider(email, authProvider)
            .orElseThrow(() -> GlobalException.CREDENTIALS_DO_NOT_EXIST);

        return LoginResponse.of(
            getAuthLoginResponse(role, member),
            personalTrainingRepository.findPtAssignedTrainerInformation(member)
        );
    }

    private AuthLoginResponse getAuthLoginResponse(Role role, Member member) {
        TokenSetDto tokenSetDto = authTokenGenerator.generateTokenSet(member.getId(), role);

        redisClient.put(
            MEMBER_REFRESH_TOKEN_PREFIX + member.getId(),
            tokenSetDto.getRefreshToken(),
            TimeUnit.SECONDS,
            tokenSetDto.getRefreshExpiredAt());

        return AuthLoginResponse.of(member.getId(), member.getEmail(), member.getName(), member.getAuthProvider(), member.getRole(), tokenSetDto);
    }
}
