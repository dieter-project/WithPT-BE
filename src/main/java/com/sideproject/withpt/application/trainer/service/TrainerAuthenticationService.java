package com.sideproject.withpt.application.trainer.service;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.TRAINER_REFRESH_TOKEN_PREFIX;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.trainer.service.dto.complex.TrainerSignUpDto;
import com.sideproject.withpt.application.trainer.service.dto.single.AcademicDto;
import com.sideproject.withpt.application.trainer.service.dto.single.AwardDto;
import com.sideproject.withpt.application.trainer.service.dto.single.CareerDto;
import com.sideproject.withpt.application.trainer.service.dto.single.CertificateDto;
import com.sideproject.withpt.application.trainer.service.dto.single.EducationDto;
import com.sideproject.withpt.application.trainer.service.dto.single.WorkScheduleDto;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
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
@Transactional(readOnly = true)
public class TrainerAuthenticationService {

    private final TrainerRepository trainerRepository;
    private final GymRepository gymRepository;

    private final AuthTokenGenerator authTokenGenerator;
    private final RedisClient redisClient;

    @Transactional
    public TokenSetDto signUp(TrainerSignUpDto signUpDto) {

        trainerRepository.findByEmail(signUpDto.getEmail())
            .ifPresent(member -> {
                throw GlobalException.ALREADY_REGISTERED_USER;
            });

        List<Gym> savedGym = signUpDto.getGyms().stream()
            .map(trainerGymScheduleDto ->
                gymRepository.findByName(trainerGymScheduleDto.getName())
                    .orElseGet(() -> gymRepository.save(trainerGymScheduleDto.toGymEntity())))
            .collect(Collectors.toList());

        List<WorkSchedule> workSchedules = signUpDto.getGyms().stream()
            .flatMap(trainerGymScheduleDto -> savedGym.stream()
                .filter(gym -> gym.getName().equals(trainerGymScheduleDto.getName()))
                .flatMap(gym -> trainerGymScheduleDto.getWorkSchedules().stream()
                    .map(WorkScheduleDto::toEntity)
                    .map(workScheduleDto -> WorkSchedule.createWorkSchedule(gym, workScheduleDto))))
            .collect(Collectors.toList());

        List<GymTrainer> gymTrainers = savedGym.stream()
            .map(GymTrainer::createGymTrainer)
            .collect(Collectors.toList());

        Long userId = trainerRepository.save(
            Trainer.createSignUpTrainer(
                signUpDto.toTrainerBasicEntity(),
                workSchedules,
                gymTrainers,
                CareerDto.toEntities(signUpDto.getCareers()),
                AcademicDto.toEntities(signUpDto.getAcademics()),
                CertificateDto.toEntities(signUpDto.getCertificates()),
                AwardDto.toEntities(signUpDto.getAwards()),
                EducationDto.toEntities(signUpDto.getEducations()))
        ).getId();

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
