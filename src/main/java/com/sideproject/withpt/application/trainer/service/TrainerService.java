package com.sideproject.withpt.application.trainer.service;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.TRAINER_REFRESH_TOKEN_PREFIX;

import com.sideproject.withpt.application.trainer.controller.dto.TrainerSignUpRequest;
import com.sideproject.withpt.application.trainer.controller.dto.TrainerSignUpRequest.CareerDto;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final AuthTokenGenerator authTokenGenerator;
    private final RedisClient redisClient;

    @Transactional
    public TokenSetDto signUp(TrainerSignUpRequest request) {
        trainerRepository.findByEmail(request.getEmail())
            .ifPresent(member -> {
                throw GlobalException.ALREADY_REGISTERED_USER;
            });

        Trainer trainer = request.toEntity();
        request.getCareers().stream()
            .map(CareerDto::toEntity)
            .forEach(trainer::addCareer);

        Long userId = trainerRepository.save(trainer).getId();
        TokenSetDto tokenSetDto = authTokenGenerator.generateTokenSet(userId, Role.TRAINER);
        redisClient.put(
            TRAINER_REFRESH_TOKEN_PREFIX + userId,
            tokenSetDto.getRefreshToken(),
            TimeUnit.SECONDS,
            tokenSetDto.getRefreshExpiredAt()
        );

        return tokenSetDto;
    }
}
