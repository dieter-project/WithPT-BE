package com.sideproject.withpt.application.schedule.service;

import com.sideproject.withpt.application.schedule.repository.ScheduleRepository;
import com.sideproject.withpt.application.trainer.service.dto.complex.GymScheduleDto;
import com.sideproject.withpt.application.trainer.service.dto.single.WorkScheduleDto;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void registerWorkSchedules(List<GymScheduleDto> gymScheduleDtos, List<GymTrainer> gymTrainers) {
        Map<String, List<WorkScheduleDto>> gymScheduleMap = createWorkScheduleDtoMap(gymScheduleDtos);

        for (GymTrainer gymTrainer : gymTrainers) {
            List<WorkScheduleDto> scheduleDtoList = gymScheduleMap.get(gymTrainer.getGym().getName());

            List<WorkSchedule> scheduleList = scheduleDtoList.stream()
                .map(workScheduleDto -> workScheduleDto.toEntity(gymTrainer))
                .collect(Collectors.toList());

            scheduleRepository.saveAll(scheduleList);
        }
    }

    private Map<String, List<WorkScheduleDto>> createWorkScheduleDtoMap(List<GymScheduleDto> gymScheduleDtos) {
        return gymScheduleDtos.stream()
            .collect(Collectors.toMap(
                GymScheduleDto::getName,
                GymScheduleDto::getWorkSchedules
            ));
    }
}
