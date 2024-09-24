package com.sideproject.withpt.application.gym.service;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.trainer.service.dto.complex.GymScheduleDto;
import com.sideproject.withpt.domain.gym.Gym;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GymService {

    private final GymRepository gymRepository;

    public Gym getGymById(Long gymId) {
        return gymRepository.findById(gymId)
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);
    }

    @Transactional
    public List<Gym> registerGyms(List<GymScheduleDto> gymSchedules) {
        return gymSchedules.stream()
            .map(this::getGymOrNewSaveBy)
            .collect(Collectors.toList());
    }

    private Gym getGymOrNewSaveBy(GymScheduleDto gymScheduleDto) {
        return gymRepository.findByName(gymScheduleDto.getName())
            .orElseGet(() -> gymRepository.save(gymScheduleDto.toGymEntity()));
    }
}
