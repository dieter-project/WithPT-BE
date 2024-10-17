package com.sideproject.withpt.application.trainer.service;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.TRAINER_REFRESH_TOKEN_PREFIX;

import com.sideproject.withpt.application.auth.service.dto.AuthLoginResponse;
import com.sideproject.withpt.application.gym.service.GymService;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.schedule.service.WorkScheduleService;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.trainer.service.dto.TrainerSignUpResponse;
import com.sideproject.withpt.application.trainer.service.dto.complex.TrainerSignUpDto;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
    private final GymTrainerRepository gymTrainerRepository;
    private final WorkScheduleService workScheduleService;

    private final AuthTokenGenerator authTokenGenerator;
    private final RedisClient redisClient;

    public AuthLoginResponse signUp(TrainerSignUpDto signUpDto) {

        TrainerSignUpResponse trainerSignUpResponse = registerTrainerWithGymsAndSchedules(signUpDto);

        TokenSetDto tokenSetDto = authTokenGenerator.generateTokenSet(trainerSignUpResponse.getId(), Role.TRAINER);

        redisClient.put(
            TRAINER_REFRESH_TOKEN_PREFIX + trainerSignUpResponse.getId(),
            tokenSetDto.getRefreshToken(),
            TimeUnit.SECONDS,
            tokenSetDto.getRefreshExpiredAt()
        );

        return AuthLoginResponse.of(trainerSignUpResponse, tokenSetDto);
    }

    @Transactional
    public TrainerSignUpResponse registerTrainerWithGymsAndSchedules(TrainerSignUpDto signUpDto) {
        if (trainerRepository.existsByEmailAndAuthProvider(signUpDto.getEmail(), signUpDto.getAuthProvider())) {
            throw GlobalException.ALREADY_REGISTERED_USER;
        }

        Trainer trainer = trainerRepository.save(signUpDto.toTrainerEntity());
        List<Gym> savedGym = gymService.registerGyms(signUpDto.getGyms());
        List<GymTrainer> gymTrainers = registerGymTrainers(savedGym, trainer);
        workScheduleService.registerWorkSchedules(signUpDto.getGyms(), gymTrainers);

        return TrainerSignUpResponse.of(trainer);
    }

    private List<GymTrainer> registerGymTrainers(List<Gym> gyms, Trainer trainer) {
        return gymTrainerRepository.saveAll(
            gyms.stream()
                .map(gym -> GymTrainer.createGymTrainer(gym, trainer))
                .collect(Collectors.toList())
        );
    }

}
