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
import com.sideproject.withpt.domain.gym.WorkSchedule;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.List;
import java.util.Map;
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
public class TrainerService {

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

        Map<Gym, List<WorkSchedule>> mappedGymSchedule = savedGym.stream()
            .collect(Collectors.toMap(
                gym -> gym,
                gym -> signUpDto.getGyms().stream()
                    .filter(trainerGymScheduleDto -> trainerGymScheduleDto.getName().equals(gym.getName()))
                    .flatMap(trainerGymScheduleDto -> trainerGymScheduleDto.getWorkSchedules().stream()
                        .map(WorkScheduleDto::toEntity))
                    .collect(Collectors.toList())
                , (workSchedules1, workSchedules2) -> workSchedules2
            ));

        
        List<GymTrainer> GymTrainers = mappedGymSchedule.keySet().stream()
            .map(gym -> GymTrainer.createGymTrainer(gym, mappedGymSchedule.get(gym)))
            .collect(Collectors.toList());

        Long userId = trainerRepository.save(
            Trainer.createSignUpTrainer(
                signUpDto.toTrainerBasicEntity(),
                GymTrainers,
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
