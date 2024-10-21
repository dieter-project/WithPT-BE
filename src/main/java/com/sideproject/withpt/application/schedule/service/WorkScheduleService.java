package com.sideproject.withpt.application.schedule.service;

import static com.sideproject.withpt.application.schedule.exception.ScheduleErrorCode.IS_EXIST_SAME_WEEKDAY;
import static com.sideproject.withpt.application.schedule.exception.ScheduleErrorCode.WORK_SCHEDULE_NOT_FOUND;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gym.service.GymService;
import com.sideproject.withpt.application.gymtrainer.exception.GymTrainerException;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.schedule.controller.request.WorkScheduleEditRequest;
import com.sideproject.withpt.application.schedule.controller.request.WorkScheduleEditRequest.Schedule;
import com.sideproject.withpt.application.schedule.service.response.WorkScheduleResponse;
import com.sideproject.withpt.application.schedule.exception.ScheduleException;
import com.sideproject.withpt.application.schedule.repository.WorkScheduleRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.application.trainer.service.dto.complex.GymScheduleDto;
import com.sideproject.withpt.application.trainer.service.dto.single.WorkScheduleDto;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final TrainerService trainerService;
    private final TrainerRepository trainerRepository;
    private final GymService gymService;
    private final GymRepository gymRepository;
    private final GymTrainerRepository gymTrainerRepository;

    public List<WorkScheduleResponse> getAllWorkScheduleByGym(Long gymId, Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Gym gym = gymRepository.findById(gymId)
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);

        GymTrainer gymTrainer = gymTrainerRepository.findByTrainerAndGym(trainer, gym)
            .orElseThrow(() -> GymTrainerException.GYM_TRAINER_NOT_MAPPING);

        return WorkScheduleResponse.of(
            workScheduleRepository.findAllByGymTrainer(gymTrainer)
        );
    }

    public WorkScheduleResponse getWorkSchedule(Long scheduleId) {
        return WorkScheduleResponse.of(
            workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(WORK_SCHEDULE_NOT_FOUND))
        );
    }

    @Transactional
    public void editWorkSchedule(Long trainerId, Long gymId, WorkScheduleEditRequest request) {

        // 같은 요일이 2건 이상이면 에러
        validExistSameWeekDay(request);

        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

        List<WorkSchedule> originWorkSchedules = workScheduleRepository.findAllByTrainerAndGym(trainer, gym);
        List<WorkScheduleEditRequest.Schedule> editWorkSchedules = request.getWorkSchedules();

        // id 가 존재하면 수정
        originWorkSchedules.forEach(originWorkSchedule -> {
            editWorkSchedules.stream()
                .filter(workSchedule -> Objects.equals(originWorkSchedule.getId(), workSchedule.getId()))
                .findFirst()
                .ifPresent(workSchedule ->
                    originWorkSchedule.editWorkScheduleTime(workSchedule.getInTime(), workSchedule.getOutTime())
                );
        });

        // id 없이 요일, 시간이 들어오면 새로 저장
        workScheduleRepository.saveAll(
            editWorkSchedules.stream()
                .filter(schedule -> schedule.getId() == null)
                .map(schedule -> Schedule.toEntity(schedule, trainer, gym))
                .collect(Collectors.toList())
        );

        // 원본 list에서 없는 id 가 있으면 삭제
        workScheduleRepository.deleteAllByIdInBatch(
            extractRemovedSchedules(originWorkSchedules, editWorkSchedules)
        );
    }

    @Transactional
    public void registerWorkSchedules(List<GymScheduleDto> gymScheduleDtos, List<GymTrainer> gymTrainers) {
        Map<String, List<WorkScheduleDto>> gymScheduleMap = createWorkScheduleDtoMap(gymScheduleDtos);

        for (GymTrainer gymTrainer : gymTrainers) {
            List<WorkScheduleDto> scheduleDtoList = gymScheduleMap.get(gymTrainer.getGym().getName());

            List<WorkSchedule> scheduleList = scheduleDtoList.stream()
                .map(workScheduleDto -> workScheduleDto.toEntity(gymTrainer))
                .collect(Collectors.toList());

            workScheduleRepository.saveAll(scheduleList);
        }
    }

    private List<Long> extractRemovedSchedules(List<WorkSchedule> originWorkSchedules, List<Schedule> editWorkSchedules) {
        return originWorkSchedules.stream()
            .map(WorkSchedule::getId)
            .filter(id -> !editWorkSchedules.stream()
                .map(Schedule::getId)
                .collect(Collectors.toList()).contains(id))
            .collect(Collectors.toList());
    }

    private void validExistSameWeekDay(WorkScheduleEditRequest request) {
        request.getWorkSchedules().stream()
            .collect(Collectors.groupingBy(Schedule::getWeekday))
            .values().stream()
            .filter(list -> list.size() > 1)
            .findAny()
            .ifPresent(list -> {
                throw new ScheduleException(IS_EXIST_SAME_WEEKDAY);
            });
    }

    private Map<String, List<WorkScheduleDto>> createWorkScheduleDtoMap(List<GymScheduleDto> gymScheduleDtos) {
        return gymScheduleDtos.stream()
            .collect(Collectors.toMap(
                GymScheduleDto::getName,
                GymScheduleDto::getWorkSchedules
            ));
    }
}
