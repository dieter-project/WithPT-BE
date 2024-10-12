package com.sideproject.withpt.application.auth.service.trainer;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.TRAINER_REFRESH_TOKEN_PREFIX;

import com.sideproject.withpt.application.auth.infra.AuthLoginParams;
import com.sideproject.withpt.application.auth.infra.OAuthInfoResponse;
import com.sideproject.withpt.application.auth.service.AuthLoginClient;
import com.sideproject.withpt.application.auth.service.RequestOAuthInfoService;
import com.sideproject.withpt.application.auth.service.dto.AuthLoginResponse;
import com.sideproject.withpt.application.auth.service.dto.LoginResponse;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerAuthLoginClient implements AuthLoginClient {

    private final AuthTokenGenerator authTokenGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final RedisClient redisClient;

    private final TrainerRepository trainerRepository;

    @Override
    public Role role() {
        return Role.TRAINER;
    }

    @Override
    public LoginResponse login(AuthLoginParams params) {
        if (params.authProvider() == AuthProvider.EMAIL) {
            // 비밀번호 기반 인증 처리
            return passwordLogin(params);
        } else {
            OAuthInfoResponse oAuthInfoResponse = getOAuthInfo(params);

            // 회원 존재 여부 확인
            if (trainerRepository.existsByEmail(oAuthInfoResponse.getEmail())) { // true : 이미 존재하면
                return existinglogin(oAuthInfoResponse, params.registerRole());
            }

            // 신규 회원이면 email, provider, role 반환
            return LoginResponse.of(AuthLoginResponse.of(oAuthInfoResponse, params.registerRole()));
        }
    }

    private LoginResponse passwordLogin(AuthLoginParams params) {
        String email = params.email();
        String password = params.password();

        Trainer trainer = trainerRepository.findByEmailAndAuthProvider(email, params.authProvider())
            .orElseThrow(() -> GlobalException.CREDENTIALS_DO_NOT_EXIST);

        if (!trainer.getPassword().equals(password)) {
            throw GlobalException.INVALID_PASSWORD;
        }

        TokenSetDto tokenSetDto = authTokenGenerator.generateTokenSet(trainer.getId(), trainer.getRole());

        redisClient.put(
            TRAINER_REFRESH_TOKEN_PREFIX + trainer.getId(),
            tokenSetDto.getRefreshToken(),
            TimeUnit.SECONDS,
            tokenSetDto.getRefreshExpiredAt());

        return LoginResponse.of(AuthLoginResponse.of(trainer, tokenSetDto));
    }

    // 소셜 정보 획득
    private OAuthInfoResponse getOAuthInfo(AuthLoginParams params) {
        return requestOAuthInfoService.request(params);
    }

    // 이미 가입된 회원 : 토큰 발급
    private LoginResponse existinglogin(OAuthInfoResponse oAuthInfoResponse, Role role) {
        Trainer trainer = trainerRepository.findByEmailAndAuthProvider(oAuthInfoResponse.getEmail(), oAuthInfoResponse.getOAuthProvider()).get();

        TokenSetDto tokenSetDto = authTokenGenerator.generateTokenSet(trainer.getId(), role);

        redisClient.put(
            TRAINER_REFRESH_TOKEN_PREFIX + trainer.getId(),
            tokenSetDto.getRefreshToken(),
            TimeUnit.SECONDS,
            tokenSetDto.getRefreshExpiredAt());

        return LoginResponse.of(AuthLoginResponse.of(trainer, tokenSetDto));
    }

}
