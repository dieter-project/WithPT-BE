package com.sideproject.withpt.application.schedule.service;

import static com.sideproject.withpt.application.schedule.exception.ScheduleErrorCode.IS_EXIST_SAME_WEEKDAY;
import static com.sideproject.withpt.application.schedule.exception.ScheduleErrorCode.WORK_SCHEDULE_NOT_FOUND;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.exception.GymTrainerException;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.schedule.controller.request.WorkScheduleEditRequest;
import com.sideproject.withpt.application.schedule.controller.request.WorkScheduleEditRequest.Schedule;
import com.sideproject.withpt.application.schedule.exception.ScheduleException;
import com.sideproject.withpt.application.schedule.repository.WorkScheduleRepository;
import com.sideproject.withpt.application.schedule.service.response.WorkScheduleResponse;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.trainer.service.dto.complex.GymScheduleDto;
import com.sideproject.withpt.application.trainer.service.dto.single.WorkScheduleDto;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final TrainerRepository trainerRepository;
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

        // 요청에서 같은 요일이 2건 이상이면 에러
        validateUniqueWeekdays(request);

        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Gym gym = gymRepository.findById(gymId)
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);

        GymTrainer gymTrainer = gymTrainerRepository.findByTrainerAndGym(trainer, gym)
            .orElseThrow(() -> GymTrainerException.GYM_TRAINER_NOT_MAPPING);

        List<WorkSchedule> originWorkSchedules = workScheduleRepository.findAllByGymTrainer(gymTrainer);
        List<WorkScheduleEditRequest.Schedule> editWorkSchedules = request.getWorkSchedules();

        // id 가 존재하면 수정
        updateWorkSchedule(originWorkSchedules, editWorkSchedules);

        // id 없이 요일, 시간이 들어오면 새로 저장
        saveNewSchedulesWithoutId(editWorkSchedules, gymTrainer);

        // 원본 스케줄과 변경 요청된 스케줄을 비교해서 매칭되는 ID 가 없으면 삭제
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

    private void validateUniqueWeekdays(WorkScheduleEditRequest request) {

        Map<Day, List<Schedule>> weekdayGroups = request.getWorkSchedules().stream()
            .collect(Collectors.groupingBy(Schedule::getWeekday));

        weekdayGroups.forEach((weekday, schedules) -> {
            if (schedules.size() > 1) {
                throw new ScheduleException(IS_EXIST_SAME_WEEKDAY);
            }
        });
    }

    private void updateWorkSchedule(List<WorkSchedule> originWorkSchedules, List<Schedule> editWorkSchedules) {
        // editWorkSchedules를 Map으로 변환 (id -> Schedule)
        Map<Long, Schedule> editScheduleMap = editWorkSchedules.stream()
            .filter(schedule -> schedule.getId() != null) // null id 필터링
            .collect(Collectors.toMap(Schedule::getId, schedule -> schedule));

        originWorkSchedules.forEach(originWorkSchedule -> {
            Schedule matchingSchedule = editScheduleMap.get(originWorkSchedule.getId());  // id로 바로 검색
            if (matchingSchedule != null) { // 일치하는 Schedule이 있을 경우 수정
                originWorkSchedule.editWorkScheduleTime(matchingSchedule.getInTime(), matchingSchedule.getOutTime());
            }
        });
    }

    private void saveNewSchedulesWithoutId(List<Schedule> editWorkSchedules, GymTrainer gymTrainer) {
        // id가 없는 새 Schedule만 필터링 후 저장
        List<WorkSchedule> newSchedules = editWorkSchedules.stream()
            .filter(schedule -> schedule.getId() == null)
            .map(schedule -> schedule.toEntity(gymTrainer))
            .collect(Collectors.toList());

        // 새로 저장할 항목이 있는 경우에만 저장 처리
        if (!newSchedules.isEmpty()) {
            workScheduleRepository.saveAll(newSchedules);
        }
    }

    private List<Long> extractRemovedSchedules(List<WorkSchedule> originWorkSchedules, List<Schedule> editWorkSchedules) {
        Set<Long> editScheduleIds = editWorkSchedules.stream()
            .map(Schedule::getId)
            .collect(Collectors.toSet());

        return originWorkSchedules.stream()
            .map(WorkSchedule::getId)
            .filter(id -> !editScheduleIds.contains(id))
            .collect(Collectors.toList());
    }

    private Map<String, List<WorkScheduleDto>> createWorkScheduleDtoMap(List<GymScheduleDto> gymScheduleDtos) {
        return gymScheduleDtos.stream()
            .collect(Collectors.toMap(
                GymScheduleDto::getName,
                GymScheduleDto::getWorkSchedules
            ));
    }
}
