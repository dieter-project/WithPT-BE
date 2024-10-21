package com.sideproject.withpt.application.gym.service;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gym.service.response.GymResponse;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.trainer.service.dto.complex.GymScheduleDto;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GymService {

    private final TrainerRepository trainerRepository;
    private final GymRepository gymRepository;

    public Slice<GymResponse> listOfAllGymsByPageable(Long trainerId, Pageable pageable) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        Slice<Gym> gyms = gymRepository.findAllTrainerGymsByPageable(trainer, pageable);
        return new SliceImpl<>(convertGymsToGymResponses(gyms), pageable, gyms.hasNext());
    }

    @Transactional
    public List<Gym> registerGyms(List<GymScheduleDto> gymSchedules) {
        return gymSchedules.stream()
            .map(this::getGymOrNewSaveBy)
            .collect(Collectors.toList());
    }

    private List<GymResponse> convertGymsToGymResponses(Slice<Gym> gyms) {
        return gyms.getContent().stream()
            .map(GymResponse::of)
            .collect(Collectors.toList());
    }

    private Gym getGymOrNewSaveBy(GymScheduleDto gymScheduleDto) {
        return gymRepository.findByName(gymScheduleDto.getName())
            .orElseGet(() -> gymRepository.save(gymScheduleDto.toGymEntity()));
    }
}
