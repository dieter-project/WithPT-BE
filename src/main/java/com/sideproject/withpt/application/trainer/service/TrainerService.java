package com.sideproject.withpt.application.trainer.service;

import com.sideproject.withpt.application.trainer.controller.dto.TrainerSignUpRequest;
import com.sideproject.withpt.application.trainer.controller.dto.TrainerSignUpRequest.CareerDto;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.domain.trainer.Trainer;
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

        return authTokenGenerator.generateTokenSet(
            trainerRepository.save(trainer).getId(),
            Role.TRAINER
        );
    }
}
