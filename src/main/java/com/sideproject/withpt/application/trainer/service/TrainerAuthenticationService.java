package com.sideproject.withpt.application.trainer.service;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.TRAINER_REFRESH_TOKEN_PREFIX;

import com.sideproject.withpt.application.auth.controller.dto.OAuthLoginResponse;
import com.sideproject.withpt.application.gym.service.GymService;
import com.sideproject.withpt.application.gymtrainer.GymTrainerService;
import com.sideproject.withpt.application.schedule.service.WorkScheduleService;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.trainer.service.dto.complex.TrainerSignUpDto;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerAuthenticationService {

    private final TrainerRepository trainerRepository;
    private final GymService gymService;
    private final GymTrainerService gymTrainerService;
    private final WorkScheduleService workScheduleService;

    private final AuthTokenGenerator authTokenGenerator;
    private final RedisClient redisClient;

    public OAuthLoginResponse signUp(TrainerSignUpDto signUpDto) {

        Trainer trainer = registerTrainerWithGymsAndSchedules(signUpDto);

        TokenSetDto tokenSetDto = authTokenGenerator.generateTokenSet(trainer.getId(), Role.TRAINER);

        redisClient.put(
            TRAINER_REFRESH_TOKEN_PREFIX + trainer.getId(),
            tokenSetDto.getRefreshToken(),
            TimeUnit.SECONDS,
            tokenSetDto.getRefreshExpiredAt()
        );

        return OAuthLoginResponse.of(trainer, tokenSetDto);
    }

    @Transactional
    public Trainer registerTrainerWithGymsAndSchedules(TrainerSignUpDto signUpDto) {
        if (trainerRepository.existsByEmail(signUpDto.getEmail())) {
            throw GlobalException.ALREADY_REGISTERED_USER;
        }

        Trainer trainer = trainerRepository.save(signUpDto.toTrainerEntity());
        List<Gym> savedGym = gymService.registerGyms(signUpDto.getGyms());
        List<GymTrainer> gymTrainers = gymTrainerService.registerGymTrainers(savedGym, trainer);
        workScheduleService.registerWorkSchedules(signUpDto.getGyms(), gymTrainers);

        return trainer;
    }

}
