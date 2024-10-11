package com.sideproject.withpt.application.auth.service.trainer;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.TRAINER_REFRESH_TOKEN_PREFIX;

import com.sideproject.withpt.application.auth.controller.dto.OAuthLoginResponse;
import com.sideproject.withpt.application.auth.infra.OAuthInfoResponse;
import com.sideproject.withpt.application.auth.infra.AuthLoginParams;
import com.sideproject.withpt.application.auth.service.OAuthLoginClient;
import com.sideproject.withpt.application.auth.service.RequestOAuthInfoService;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerOAuthLoginClient implements OAuthLoginClient {

    private final AuthTokenGenerator authTokenGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final RedisClient redisClient;

    private final TrainerRepository trainerRepository;

    @Override
    public Role role() {
        return Role.TRAINER;
    }

    @Override
    public OAuthLoginResponse login(AuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = getOAuthInfo(params);

        // 회원 존재 여부 확인
        if (trainerRepository.existsByEmail(oAuthInfoResponse.getEmail())) { // true : 이미 존재하면
            return existinglogin(oAuthInfoResponse, params.registerRole());
        }

        // 신규 회원이면 email, provider, role 반환
        return OAuthLoginResponse.of(oAuthInfoResponse, params.registerRole());
    }

    // 소셜 정보 획득
    private OAuthInfoResponse getOAuthInfo(AuthLoginParams params) {
        return requestOAuthInfoService.request(params);
    }

    // 이미 가입된 회원 : 토큰 발급
    private OAuthLoginResponse existinglogin(OAuthInfoResponse oAuthInfoResponse, Role role) {
        Trainer trainer = trainerRepository.findByEmail(oAuthInfoResponse.getEmail()).get();

        TokenSetDto tokenSetDto = authTokenGenerator.generateTokenSet(trainer.getId(), role);

        redisClient.put(
            TRAINER_REFRESH_TOKEN_PREFIX + trainer.getId(),
            tokenSetDto.getRefreshToken(),
            TimeUnit.SECONDS,
            tokenSetDto.getRefreshExpiredAt());

        return OAuthLoginResponse.of(trainer, tokenSetDto);
    }
}
