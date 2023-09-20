package com.sideproject.withpt.application.gym.service;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.domain.gym.Gym;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GymService {

    private final GymRepository gymRepository;

    public Gym getGymById(Long gymId) {
        return gymRepository.findById(gymId)
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);
    }
}
